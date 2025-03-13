package app.scit46.ufc.repository.tag;

import app.scit46.ufc.entity.product.ProductTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductTagRepository extends JpaRepository<ProductTagEntity, Long> {
    List<ProductTagEntity> findTagsByProduct_ProductId(Long productId);

    @Query(value = """
                SELECT t.content FROM Tags t
                JOIN ProductTags pt ON t.tag_id = pt.tag_id
                WHERE pt.product_id = :productId
            """, nativeQuery = true)
    List<String> findTagsByProductId(@Param("productId") Long productId);

    @Query("SELECT t.tagId FROM TagEntity t WHERE t.content = :content")
    Optional<Integer> findTagIdByContent(@Param("content") String content);

    // ✅ 수정 코드: 올바른 필드명 사용
    @Modifying
    @Transactional
    @Query("DELETE FROM ProductTagEntity pt WHERE pt.product.productId = :productId AND pt.tag.tagId = :tagId")
    void deleteByProductIdAndTagId(@Param("productId") Long productId, @Param("tagId") Integer tagId);

    // ✅ 올바른 코드 (product 필드를 기준으로 조회)
    @Query("SELECT pt FROM ProductTagEntity pt WHERE pt.product.productId = :productId")
    List<ProductTagEntity> findByProductId(@Param("productId") Long productId);
}
