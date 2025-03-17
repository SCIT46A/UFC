package app.scit46.ufc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.scit46.ufc.entity.product.ProductPaymentEntity;

public interface ProductPaymentRepository extends JpaRepository<ProductPaymentEntity, Long> {
    List<ProductPaymentEntity> findByPurchasedByUserId(Long userId);
}
