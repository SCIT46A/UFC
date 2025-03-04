package app.scit46.ufc.repository;

import app.scit46.ufc.dto.SearchResultDTO;
import app.scit46.ufc.dto.custom.IntroPageCampaignDTO;
import app.scit46.ufc.entity.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<SearchEntity, Long> {

    @Query("SELECT s FROM SearchEntity s WHERE LOWER(REPLACE(s.name, ' ', '')) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SearchEntity> searchByKeyword(@Param("keyword") String keyword);

    // 전체 검색 (캠페인 + 제품) 쿼리 예시
    @Query(value = "SELECT * FROM ( " +
            "  SELECT " +
            "    CAST(c.campaign_id AS SIGNED) AS originalId, " +
            "    'campaign' AS type, " +
            "    (SELECT iu.image_id FROM ImageUrls iu WHERE iu.photo_id = c.photo_id) AS imageId, " +
            "    (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = c.created_by) AS sellerName, " +
            "    c.title AS title, " +
            "    c.description AS description, " +
            "    NULL AS price, " +
            "    CAST(DATEDIFF(c.end_date, CURRENT_DATE) AS SIGNED) AS remainingDays, " +
            "    CAST(IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id), 0) AS SIGNED) AS donatedQuantity, " +
            "    IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id) * 100.0 / " +
            "           NULLIF((SELECT SUM(cg.quantity_required) FROM CampaignGoals cg WHERE cg.campaign_id = c.campaign_id), 0), 0) AS donationPercentage, " +
            "    c.created_date AS createdDate, " +
            "    CAST((SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) AS SIGNED) AS likes, " +
            "    (SELECT GROUP_CONCAT(t.content) FROM CampaignTags ct " +
            "         JOIN Tags t ON ct.tag_id = t.tag_id " +
            "         WHERE ct.campaign_id = c.campaign_id) AS tags, " +
            "    (CASE WHEN :userLoginId IS NULL THEN 0 ELSE (EXISTS (SELECT 1 FROM Likes l " +
            "         WHERE l.campaign_id = c.campaign_id AND l.user_id = :userLoginId) + 0) END) AS isLiked " +
            "  FROM Campaigns c " +
            "  WHERE c.title LIKE CONCAT('%', :keyword, '%') " +
            "  UNION " +
            "  SELECT " +
            "    CAST(p.product_id AS SIGNED) AS originalId, " +
            "    'product' AS type, " +
            "    (SELECT iu.image_id FROM ImageUrls iu WHERE iu.photo_id = i.photo_id) AS imageId, " +
            "    (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = p.created_by) AS sellerName, " +
            "    i.name AS title, " +
            "    i.description AS description, " +
            "    p.price AS price, " +
            "    NULL AS remainingDays, " +
            "    NULL AS donatedQuantity, " +
            "    NULL AS donationPercentage, " +
            "    NULL AS createdDate, " +
            "    CAST((SELECT COUNT(*) FROM Likes l WHERE l.product_id = p.product_id) AS SIGNED) AS likes, " +
            "    (SELECT GROUP_CONCAT(t.content) FROM ProductTags pt " +
            "         JOIN Tags t ON pt.tag_id = t.tag_id " +
            "         WHERE pt.product_id = p.product_id) AS tags, " +
            "    (CASE WHEN :userLoginId IS NULL THEN 0 ELSE (EXISTS (SELECT 1 FROM Likes l " +
            "         WHERE l.product_id = p.product_id AND l.user_id = :userLoginId) + 0) END) AS isLiked " +
            "  FROM Products p " +
            "  INNER JOIN Items i ON i.item_id = p.item_id " +
            "  WHERE i.name LIKE CONCAT('%', :keyword, '%') " +
            ") AS results",
            nativeQuery = true)
    List<SearchResultDTO> findSearchResults(@Param("keyword") String keyword, @Param("userLoginId") Long userLoginId);



    // ② 진행 중인 캠페인: 검색어 없이 전체 데이터를 불러옴 (좋아요 여부 추가)
    @Query(value = "SELECT " +
            "  CAST(c.campaign_id AS SIGNED) AS originalId, " +
            "  'campaign' AS type, " +
            "  (SELECT iu.image_id FROM ImageUrls iu WHERE iu.photo_id = c.photo_id) AS imageId, " +
            "  (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = c.created_by) AS sellerName, " +
            "  c.title AS title, " +
            "  c.description AS description, " +
            "  NULL AS price, " +
            "  CAST(DATEDIFF(c.end_date, CURRENT_DATE) AS SIGNED) AS remainingDays, " +
            "  CAST(IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id), 0) AS SIGNED) AS donatedQuantity, " +
            "  IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id) * 100.0 / " +
            "         NULLIF((SELECT SUM(cg.quantity_required) FROM CampaignGoals cg WHERE cg.campaign_id = c.campaign_id), 0), 0) AS donationPercentage, " +
            "  c.created_date AS createdDate, " +
            "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) AS SIGNED) AS likes, " +
            "  (SELECT GROUP_CONCAT(t.content) FROM CampaignTags ct " +
            "         JOIN Tags t ON ct.tag_id = t.tag_id " +
            "         WHERE ct.campaign_id = c.campaign_id) AS tags, " +
            "  (CASE WHEN :userLoginId IS NULL THEN 0 ELSE (EXISTS (SELECT 1 FROM Likes l " +
            "         WHERE l.campaign_id = c.campaign_id AND l.user_id = :userLoginId) + 0) END) AS isLiked " +
            "FROM Campaigns c " +
            "WHERE c.start_date <= CURRENT_DATE " +
            "  AND c.end_date >= CURRENT_DATE",
            nativeQuery = true)
    List<SearchResultDTO> findOngoingCampaigns(@Param("userLoginId") Long userLoginId);

    // ③ 진행 예정인 캠페인: 검색어 없이 전체 데이터를 불러옴 (좋아요 여부 추가)
    @Query(value = "SELECT " +
            "  CAST(c.campaign_id AS SIGNED) AS originalId, " +
            "  'campaign' AS type, " +
            "  (SELECT iu.image_id FROM ImageUrls iu WHERE iu.photo_id = c.photo_id) AS imageId, " +
            "  (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = c.created_by) AS sellerName, " +
            "  c.title AS title, " +
            "  c.description AS description, " +
            "  NULL AS price, " +
            "  CAST(DATEDIFF(c.end_date, CURRENT_DATE) AS SIGNED) AS remainingDays, " +
            "  CAST(IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id), 0) AS SIGNED) AS donatedQuantity, " +
            "  IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id) * 100.0 / " +
            "         NULLIF((SELECT SUM(cg.quantity_required) FROM CampaignGoals cg WHERE cg.campaign_id = c.campaign_id), 0), 0) AS donationPercentage, " +
            "  c.created_date AS createdDate, " +
            "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) AS SIGNED) AS likes, " +
            "  (SELECT GROUP_CONCAT(t.content) FROM CampaignTags ct " +
            "         JOIN Tags t ON ct.tag_id = t.tag_id " +
            "         WHERE ct.campaign_id = c.campaign_id) AS tags, " +
            "  (CASE WHEN :userLoginId IS NULL THEN 0 ELSE (EXISTS (SELECT 1 FROM Likes l " +
            "         WHERE l.campaign_id = c.campaign_id AND l.user_id = :userLoginId) + 0) END) AS isLiked " +
            "FROM Campaigns c " +
            "WHERE c.start_date > CURRENT_DATE",
            nativeQuery = true)
    List<SearchResultDTO> findUpcomingCampaigns(@Param("userLoginId") Long userLoginId);

    // ④ 판매 (제품) 조회: 검색어 없이 전체 데이터를 불러옴 (좋아요 여부 추가)
    @Query(value = "SELECT " +
            "  CAST(p.product_id AS SIGNED) AS originalId, " +
            "  'product' AS type, " +
            "  (SELECT iu.image_id FROM ImageUrls iu WHERE iu.photo_id = i.photo_id) AS imageId, " +
            "  (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = p.created_by) AS sellerName, " +
            "  i.name AS title, " +
            "  i.description AS description, " +
            "  p.price AS price, " +
            "  NULL AS remainingDays, " +
            "  NULL AS donatedQuantity, " +
            "  NULL AS donationPercentage, " +
            "  NULL AS createdDate, " +  // 제품에는 createdDate 없음
            "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.product_id = p.product_id) AS SIGNED) AS likes, " +
            "  (SELECT GROUP_CONCAT(t.content) FROM ProductTags pt " +
            "         JOIN Tags t ON pt.tag_id = t.tag_id " +
            "         WHERE pt.product_id = p.product_id) AS tags, " +
            "  (CASE WHEN :userLoginId IS NULL THEN 0 ELSE (EXISTS (SELECT 1 FROM Likes l " +
            "         WHERE l.product_id = p.product_id AND l.user_id = :userLoginId) + 0) END) AS isLiked " +
            "FROM Products p " +
            "INNER JOIN Items i ON i.item_id = p.item_id",
            nativeQuery = true)
    List<SearchResultDTO> findSales(@Param("userLoginId") Long userLoginId);

    /**
     * 캠페인 좋아요 많은 순 상위 10개 조회 (좋아요 여부 추가)
     */
    @Query(value = "SELECT " +
            "  CAST(c.campaign_id AS SIGNED) AS originalId, " +
            "  'campaign' AS type, " +
            "  (SELECT iu.image_id FROM ImageUrls iu WHERE iu.photo_id = c.photo_id) AS imageId, " +
            "  (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = c.created_by) AS sellerName, " +
            "  c.title AS title, " +
            "  c.description AS description, " +
            "  NULL AS price, " +
            "  CAST(DATEDIFF(c.end_date, CURRENT_DATE) AS SIGNED) AS remainingDays, " +
            "  CAST(IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id), 0) AS SIGNED) AS donatedQuantity, " +
            "  IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id) * 100.0 / " +
            "         NULLIF((SELECT SUM(cg.quantity_required) FROM CampaignGoals cg WHERE cg.campaign_id = c.campaign_id), 0), 0) AS donationPercentage, " +
            "  c.created_date AS createdDate, " +
            "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) AS SIGNED) AS likes, " +
            "  (SELECT GROUP_CONCAT(t.content) FROM CampaignTags ct " +
            "         JOIN Tags t ON ct.tag_id = t.tag_id " +
            "         WHERE ct.campaign_id = c.campaign_id) AS tags, " +
            "  (CASE WHEN :userLoginId IS NULL THEN 0 ELSE (EXISTS (SELECT 1 FROM Likes l " +
            "         WHERE l.campaign_id = c.campaign_id AND l.user_id = :userLoginId) + 0) END) AS isLiked " +
            "FROM Campaigns c " +
            "ORDER BY (SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) DESC " +
            "LIMIT 10",
            nativeQuery = true)
    List<SearchResultDTO> findTop10CampaignsByLikes(@Param("userLoginId") Long userLoginId);

    /**
     * 제품 좋아요 많은 순 상위 10개 조회 (좋아요 여부 추가)
     */
    @Query(value = "SELECT " +
            "  CAST(p.product_id AS SIGNED) AS originalId, " +
            "  'product' AS type, " +
            "  (SELECT iu.image_id FROM ImageUrls iu WHERE iu.photo_id = i.photo_id) AS imageId, " +
            "  (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = p.created_by) AS sellerName, " +
            "  i.name AS title, " +
            "  i.description AS description, " +
            "  p.price AS price, " +
            "  NULL AS remainingDays, " +
            "  NULL AS donatedQuantity, " +
            "  NULL AS donationPercentage, " +
            "  NULL AS createdDate, " +
            "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.product_id = p.product_id) AS SIGNED) AS likes, " +
            "  (SELECT GROUP_CONCAT(t.content) FROM ProductTags pt " +
            "         JOIN Tags t ON pt.tag_id = t.tag_id " +
            "         WHERE pt.product_id = p.product_id) AS tags, " +
            "  (CASE WHEN :userLoginId IS NULL THEN 0 ELSE (EXISTS (SELECT 1 FROM Likes l " +
            "         WHERE l.product_id = p.product_id AND l.user_id = :userLoginId) + 0) END) AS isLiked " +
            "FROM Products p " +
            "INNER JOIN Items i ON i.item_id = p.item_id " +
            "ORDER BY (SELECT COUNT(*) FROM Likes l WHERE l.product_id = p.product_id) DESC " +
            "LIMIT 10",
            nativeQuery = true)
    List<SearchResultDTO> findTop10ProductsByLikes(@Param("userLoginId") Long userLoginId);








}
