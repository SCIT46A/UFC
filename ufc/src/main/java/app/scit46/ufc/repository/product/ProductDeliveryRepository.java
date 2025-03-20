package app.scit46.ufc.repository.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.scit46.ufc.entity.product.ProductDeliveryEntity;

@Repository
public interface ProductDeliveryRepository extends JpaRepository<ProductDeliveryEntity, Long> {

    Optional<ProductDeliveryEntity> findPDeliveryIdByPay_PayId(Long payId);

       @Query("SELECT rw FROM ProductDeliveryEntity rw WHERE rw.pay.payId = :payId")
    ProductDeliveryEntity findProductDeliveryByPayId(@Param("payId") Long payId);
}
