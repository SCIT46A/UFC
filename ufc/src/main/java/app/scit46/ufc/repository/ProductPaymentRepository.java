package app.scit46.ufc.repository;

import app.scit46.ufc.entity.product.ProductPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPaymentRepository extends JpaRepository<ProductPaymentEntity, Long> {
}
