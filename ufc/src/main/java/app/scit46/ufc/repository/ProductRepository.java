package app.scit46.ufc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.scit46.ufc.entity.product.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByItem_NameContaining(String itemName);

    @Query(value = """
                SELECT
                    p.product_id,
                    i.name AS item_name,
                    i.description AS item_description,
                    p.stock_quantity,
                    p.price,
                    p.status,
                    p.create_time,
                    COALESCE(GROUP_CONCAT(t.content ORDER BY t.tag_id SEPARATOR ','), '') AS tags
                FROM Products p
                JOIN Items i ON p.item_id = i.item_id
                LEFT JOIN ProductTags pt ON p.product_id = pt.product_id
                LEFT JOIN Tags t ON pt.tag_id = t.tag_id
                WHERE p.created_by = :creatorId
                AND p.status != 4  -- ✅ 삭제된 상품 제외
                GROUP BY p.product_id, i.name, i.description, p.stock_quantity, p.price, p.status, p.create_time
            """, nativeQuery = true)
    List<Object[]> findProductsByCreatorId(@Param("creatorId") Long creatorId);

    @Query(value = """
                SELECT t.content FROM Tags t
                JOIN ProductTags pt ON t.tag_id = pt.tag_id
                WHERE pt.product_id = :productId
            """, nativeQuery = true)
    List<String> findTagsByProductId(@Param("productId") Long productId);

}
