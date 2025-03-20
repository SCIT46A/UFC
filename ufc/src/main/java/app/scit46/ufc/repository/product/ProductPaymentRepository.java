package app.scit46.ufc.repository.product;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.scit46.ufc.entity.product.ProductPaymentEntity;

public interface ProductPaymentRepository extends JpaRepository<ProductPaymentEntity, Long> {

    @Query(value = """
                SELECT
                    pp.pay_id AS payId,
                    p.product_id AS productId,
                    i.name AS itemName,
                    p.price AS productPrice,
                    pp.stock AS quantity,
                    u.user_id AS buyerId,
                    u.user_name AS buyerName,
                    u.phone_number AS buyerPhone,
                    u.user_address AS buyerAddress,
                    pp.purchased_date AS purchasedDate,
                    pp.status AS paymentStatus,
                    pd.p_delivery_id AS deliveryId,
                    pd.invoice AS invoice,
                    pd.status AS deliveryStatus
                FROM ProductPayments pp
                JOIN Products p ON pp.product_id = p.product_id
                JOIN Items i ON p.item_id = i.item_id
                JOIN Users u ON pp.purchased_by = u.user_id
                LEFT JOIN ProductDeliveries pd ON pp.pay_id = pd.pay_id
                WHERE p.created_by = :creatorId
            """, nativeQuery = true)
    List<Map<String, Object>> findOrdersByCreator(@Param("creatorId") Long creatorId);

    @Query(value = """
                SELECT
                pp.pay_id AS payId,
                p.product_id AS productId,
                i.name AS itemName,
                p.price AS productPrice,
                pp.stock AS quantity,
                u.user_id AS buyerId,
                u.user_name AS buyerName,
                u.phone_number AS buyerPhone,
                u.user_address AS buyerAddress,
                pp.purchased_date AS purchasedDate,
                DATE_ADD(pp.purchased_date, INTERVAL 14 DAY) AS scheduledSettlementDate,
                pp.status AS paymentStatus,
                (pp.price * pp.stock) AS totalAmount,
                (pp.price * pp.stock * 0.15) AS feeAmount,
                (pp.price * pp.stock * 0.85) AS settlementAmount,
                CASE
                    WHEN pp.is_adjust = 1 THEN 'COMPLETED'
                    WHEN DATE_ADD(pp.purchased_date, INTERVAL 14 DAY) <= NOW() THEN 'SCHEDULED'
                    ELSE 'PENDING'
                END AS settlementStatus
            FROM
                ProductPayments pp
            JOIN
                Products p ON pp.product_id = p.product_id
            JOIN
                Items i ON p.item_id = i.item_id
            JOIN
                Users u ON pp.purchased_by = u.user_id
            WHERE
                p.created_by = :creatorId
                AND pp.status = 'ordered';

            """, nativeQuery = true)
    List<Map<String, Object>> findAllSettlements(@Param("creatorId") Long creatorId);

    @Query(value = """
            SELECT
                pp.pay_id AS payId,
                pp.purchased_date AS purchasedDate,
                DATE_ADD(pp.purchased_date, INTERVAL 14 DAY) AS scheduledSettlementDate,
                (pp.price * pp.stock) AS totalAmount,
                (pp.price * pp.stock * 0.15) AS feeAmount,
                (pp.price * pp.stock * 0.85) AS settlementAmount,
                CASE
                    WHEN pp.is_adjust = 1 THEN 'COMPLETED'
                    WHEN DATE_ADD(pp.purchased_date, INTERVAL 14 DAY) <= NOW() THEN 'SCHEDULED'
                    ELSE 'PENDING'
                END AS settlementStatus
            FROM ProductPayments pp
            WHERE pp.pay_id = :payId
            """, nativeQuery = true)
    Map<String, Object> findSettlementById(@Param("payId") Long payId);

    @Query(value = """
            SELECT
                i.name AS productName,
                pp.stock AS quantity,
                p.price AS price,
                (p.price * pp.stock) AS total
            FROM ProductPayments pp
            JOIN Products p ON pp.product_id = p.product_id
            JOIN Items i ON p.item_id = i.item_id
            WHERE pp.pay_id = :payId
            """, nativeQuery = true)
    List<Map<String, Object>> findSettlementItemsById(@Param("payId") Long payId);

    @Query("SELECT p.product.productId FROM ProductPaymentEntity p WHERE p.payId = :payId")
    Optional<Long> findProductIdByPayId(@Param("payId") Long payId);

    @Query("SELECT p FROM ProductPaymentEntity p WHERE p.purchasedBy.userId = :userId")
    List<ProductPaymentEntity> findByPurchasedByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM ProductPaymentEntity p WHERE p.payId = :payId")
    Optional<ProductPaymentEntity> findByPayId(@Param("payId") Long payId);

    @Modifying
    @Query(value = """
                UPDATE ProductPayments
                SET is_adjust = 1
                WHERE pay_id IN (:payIds)
            """, nativeQuery = true)
    void markAsSettled(@Param("payIds") List<Long> payIds);

}
