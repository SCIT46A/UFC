package app.scit46.ufc.repository.product;

import app.scit46.ufc.entity.product.ProductPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Query("SELECT p.product.productId FROM ProductPaymentEntity p WHERE p.payId = :payId")
    Optional<Long> findProductIdByPayId(@Param("payId") Long payId);

    @Query("SELECT p FROM ProductPaymentEntity p WHERE p.purchasedBy.userId = :userId")
    List<ProductPaymentEntity> findByPurchasedByUserId(@Param("userId") Long userId);
}
