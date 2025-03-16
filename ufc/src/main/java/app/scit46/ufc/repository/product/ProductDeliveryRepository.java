package app.scit46.ufc.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import app.scit46.ufc.entity.product.ProductDeliveryEntity;

@Repository
public interface ProductDeliveryRepository extends JpaRepository<ProductDeliveryEntity, Long> {

    Optional<ProductDeliveryEntity> findPDeliveryIdByPay_PayId(Long payId);
}
