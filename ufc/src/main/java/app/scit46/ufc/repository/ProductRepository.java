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
                    p.stock_quantity,
                    p.status,
                    p.create_time,
                    COALESCE(GROUP_CONCAT(t.content ORDER BY t.tag_id SEPARATOR ','), '') AS tags
                FROM Products p
                JOIN Items i ON p.item_id = i.item_id
                LEFT JOIN ProductTags pt ON p.product_id = pt.product_id
                LEFT JOIN Tags t ON pt.tag_id = t.tag_id
                WHERE p.created_by = :creatorId
                GROUP BY p.product_id, i.name, p.stock_quantity, p.status, p.create_time
            """, nativeQuery = true)
    List<Object[]> findProductsByCreatorId(@Param("creatorId") Long creatorId);

}
