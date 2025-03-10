package app.scit46.ufc.repository.tag;

import app.scit46.ufc.entity.product.ProductTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductTagRepository extends JpaRepository<ProductTagEntity,Long> {
    List<ProductTagEntity> findTagsByProduct_ProductId(Long productId);
}
