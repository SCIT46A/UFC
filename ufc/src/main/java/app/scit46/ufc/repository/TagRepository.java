package app.scit46.ufc.repository;

import app.scit46.ufc.entity.SearchEntity;
import app.scit46.ufc.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Integer> {
    @Query(value = """
        SELECT t.tag_id, t.content, 
               (COUNT(ct.tag_id) + COUNT(pt.tag_id)) AS total_usage
        FROM Tags t
        LEFT JOIN CampaignTags ct ON t.tag_id = ct.tag_id
        LEFT JOIN ProductTags pt ON t.tag_id = pt.tag_id
        GROUP BY t.tag_id, t.content
        ORDER BY total_usage DESC
        LIMIT 11
    """, nativeQuery = true)
    List<Object[]> findTopTags();


    @Query("SELECT s FROM TagEntity s WHERE LOWER(REPLACE(s.content, ' ', '')) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<TagEntity> tagByKeyword(@Param("keyword") String keyword);


}
