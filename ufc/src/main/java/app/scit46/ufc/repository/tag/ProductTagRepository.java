package app.scit46.ufc.repository.tag;

import org.springframework.data.jpa.repository.JpaRepository;

import app.scit46.ufc.entity.product.ProductTagEntity;

public interface ProductTagRepository extends JpaRepository<ProductTagEntity, Long> {
    
}
