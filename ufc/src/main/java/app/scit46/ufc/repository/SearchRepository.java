package app.scit46.ufc.repository;

import app.scit46.ufc.dto.SearchResultDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
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
                        "    CAST(IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id), 0) AS SIGNED) AS donatedQuantity, "
                        +
                        "    IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id) * 100.0 / "
                        +
                        "           NULLIF((SELECT SUM(cg.quantity_required) FROM CampaignGoals cg WHERE cg.campaign_id = c.campaign_id), 0), 0) AS donationPercentage, "
                        +
                        "    c.created_date AS createdDate, " +
                        "    CAST((SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) AS SIGNED) AS likes, "
                        +
                        "    (SELECT GROUP_CONCAT(t.content) FROM CampaignTags ct " +
                        "         JOIN Tags t ON ct.tag_id = t.tag_id " +
                        "         WHERE ct.campaign_id = c.campaign_id) AS tags, " +
                        "    (CASE WHEN :userLoginId IS NULL THEN 0 ELSE (EXISTS (SELECT 1 FROM Likes l " +
                        "         WHERE l.campaign_id = c.campaign_id AND l.user_id = :userLoginId) + 0) END) AS isLiked "
                        +
                        "  FROM Campaigns c " +
                        "  WHERE c.title LIKE CONCAT('%', :keyword, '%') " +
                        "    AND c.campaign_status = 1 " + // 캠페인 상태 필터 추가
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
                        "    CAST((SELECT COUNT(*) FROM Likes l WHERE l.product_id = p.product_id) AS SIGNED) AS likes, "
                        +
                        "    (SELECT GROUP_CONCAT(t.content) FROM ProductTags pt " +
                        "         JOIN Tags t ON pt.tag_id = t.tag_id " +
                        "         WHERE pt.product_id = p.product_id) AS tags, " +
                        "    (CASE WHEN :userLoginId IS NULL THEN 0 ELSE (EXISTS (SELECT 1 FROM Likes l " +
                        "         WHERE l.product_id = p.product_id AND l.user_id = :userLoginId) + 0) END) AS isLiked "
                        +
                        "  FROM Products p " +
                        "  INNER JOIN Items i ON i.item_id = p.item_id " +
                        "  WHERE i.name LIKE CONCAT('%', :keyword, '%') " +
                        "    AND p.status = 1 " + // 제품 상태 필터 추가
                        ") AS results", nativeQuery = true)
        List<SearchResultDTO> findSearchResults(@Param("keyword") String keyword,
                        @Param("userLoginId") Long userLoginId);

        // ② 진행 중인 캠페인 (campaign_status = 1)
        /*
         * @Query(value = "SELECT " +
         * "  CAST(c.campaign_id AS SIGNED) AS originalId, " +
         * "  'campaign' AS type, " +
         * "  (SELECT iu.image_id FROM ImageUrls iu WHERE iu.photo_id = c.photo_id) AS imageId, "
         * +
         * "  (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = c.created_by) AS sellerName, "
         * +
         * "  c.title AS title, " +
         * "  c.description AS description, " +
         * "  NULL AS price, " +
         * "  CAST(DATEDIFF(c.end_date, CURRENT_DATE) AS SIGNED) AS remainingDays, " +
         * "  CAST(IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id), 0) AS SIGNED) AS donatedQuantity, "
         * +
         * "  IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id) * 100.0 / "
         * +
         * "         NULLIF((SELECT SUM(cg.quantity_required) FROM CampaignGoals cg WHERE cg.campaign_id = c.campaign_id), 0), 0) AS donationPercentage, "
         * +
         * "  c.created_date AS createdDate, " +
         * "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) AS SIGNED) AS likes, "
         * +
         * "  (SELECT GROUP_CONCAT(t.content) FROM CampaignTags ct " +
         * "         JOIN Tags t ON ct.tag_id = t.tag_id " +
         * "         WHERE ct.campaign_id = c.campaign_id) AS tags, " +
         * "  (CASE WHEN :userLoginId IS NULL THEN 0 ELSE (EXISTS (SELECT 1 FROM Likes l "
         * +
         * "         WHERE l.campaign_id = c.campaign_id AND l.user_id = :userLoginId) + 0) END) AS isLiked "
         * +
         * "FROM Campaigns c " +
         * "WHERE c.start_date <= CURRENT_DATE " +
         * "  AND c.end_date >= CURRENT_DATE " +
         * "  AND c.campaign_status = 1", // 캠페인 상태 필터 추가
         * nativeQuery = true)
         * List<SearchResultDTO> findOngoingCampaigns_add(@Param("userLoginId") Long
         * userLoginId);
         */

        // ② 진행 중인 캠페인 (campaign_status = 1)
        @Query(value = "SELECT " +
                        "  CAST(c.campaign_id AS SIGNED) AS originalId, " +
                        "  'campaign' AS type, " +
                        "  (SELECT iu.image_id FROM ImageUrls iu WHERE iu.photo_id = c.photo_id) AS imageId, " +
                        "  (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = c.created_by) AS sellerName, " +
                        "  c.title AS title, " +
                        "  c.description AS description, " +
                        "  NULL AS price, " +
                        "  CAST(DATEDIFF(c.end_date, CURRENT_DATE) AS SIGNED) AS remainingDays, " +
                        "  CAST(IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id), 0) AS SIGNED) AS donatedQuantity, "
                        +
                        "  IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id) * 100.0 / "
                        +
                        "         NULLIF((SELECT SUM(cg.quantity_required) FROM CampaignGoals cg WHERE cg.campaign_id = c.campaign_id), 0), 0) AS donationPercentage, "
                        +
                        "  c.created_date AS createdDate, " +
                        "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) AS SIGNED) AS likes, "
                        +
                        "  (SELECT GROUP_CONCAT(t.content) FROM CampaignTags ct " +
                        "         JOIN Tags t ON ct.tag_id = t.tag_id " +
                        "         WHERE ct.campaign_id = c.campaign_id) AS tags, " +
                        "  (CASE WHEN :userLoginId IS NULL THEN 0 ELSE (EXISTS (SELECT 1 FROM Likes l " +
                        "         WHERE l.campaign_id = c.campaign_id AND l.user_id = :userLoginId) + 0) END) AS isLiked "
                        +
                        "FROM Campaigns c " +
                        "WHERE c.start_date <= CURRENT_DATE " +
                        "  AND c.end_date >= CURRENT_DATE " +
                        "  AND c.campaign_status = 1", // 캠페인 상태 필터 추가
                        nativeQuery = true)
        List<SearchResultDTO> findOngoingCampaigns(@Param("userLoginId") Long userLoginId);

        // ③ 진행 예정인 캠페인 (campaign_status = 1)
        /*
         * @Query(value = "SELECT " +
         * "  CAST(c.campaign_id AS SIGNED) AS originalId, " +
         * "  'campaign' AS type, " +
         * "  (SELECT iu.image_id FROM ImageUrls iu WHERE iu.photo_id = c.photo_id) AS imageId, "
         * +
         * "  (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = c.created_by) AS sellerName, "
         * +
         * "  c.title AS title, " +
         * "  c.description AS description, " +
         * "  NULL AS price, " +
         * "  CAST(DATEDIFF(c.end_date, CURRENT_DATE) AS SIGNED) AS remainingDays, " +
         * "  CAST(IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id), 0) AS SIGNED) AS donatedQuantity, "
         * +
         * "  IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id) * 100.0 / "
         * +
         * "         NULLIF((SELECT SUM(cg.quantity_required) FROM CampaignGoals cg WHERE cg.campaign_id = c.campaign_id), 0), 0) AS donationPercentage, "
         * +
         * "  c.created_date AS createdDate, " +
         * "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) AS SIGNED) AS likes, "
         * +
         * "  (SELECT GROUP_CONCAT(t.content) FROM CampaignTags ct " +
         * "         JOIN Tags t ON ct.tag_id = t.tag_id " +
         * "         WHERE ct.campaign_id = c.campaign_id) AS tags, " +
         * "  (CASE WHEN :userLoginId IS NULL THEN 0 ELSE (EXISTS (SELECT 1 FROM Likes l "
         * +
         * "         WHERE l.campaign_id = c.campaign_id AND l.user_id = :userLoginId) + 0) END) AS isLiked "
         * +
         * "FROM Campaigns c " +
         * "WHERE c.start_date > CURRENT_DATE " +
         * "  AND c.campaign_status = 1", // 캠페인 상태 필터 추가
         * nativeQuery = true)
         * List<SearchResultDTO> findUpcomingCampaigns_add(@Param("userLoginId") Long
         * userLoginId);
         */

        // ③ 진행 예정인 캠페인 (campaign_status = 1)
        @Query(value = "SELECT " +
                        "  CAST(c.campaign_id AS SIGNED) AS originalId, " +
                        "  'campaign' AS type, " +
                        "  (SELECT iu.image_id FROM ImageUrls iu WHERE iu.photo_id = c.photo_id) AS imageId, " +
                        "  (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = c.created_by) AS sellerName, " +
                        "  c.title AS title, " +
                        "  c.description AS description, " +
                        "  NULL AS price, " +
                        "  CAST(DATEDIFF(c.end_date, CURRENT_DATE) AS SIGNED) AS remainingDays, " +
                        "  CAST(IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id), 0) AS SIGNED) AS donatedQuantity, "
                        +
                        "  IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id) * 100.0 / "
                        +
                        "         NULLIF((SELECT SUM(cg.quantity_required) FROM CampaignGoals cg WHERE cg.campaign_id = c.campaign_id), 0), 0) AS donationPercentage, "
                        +
                        "  c.created_date AS createdDate, " +
                        "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) AS SIGNED) AS likes, "
                        +
                        "  (SELECT GROUP_CONCAT(t.content) FROM CampaignTags ct " +
                        "         JOIN Tags t ON ct.tag_id = t.tag_id " +
                        "         WHERE ct.campaign_id = c.campaign_id) AS tags, " +
                        "  (CASE WHEN :userLoginId IS NULL THEN 0 ELSE (EXISTS (SELECT 1 FROM Likes l " +
                        "         WHERE l.campaign_id = c.campaign_id AND l.user_id = :userLoginId) + 0) END) AS isLiked "
                        +
                        "FROM Campaigns c " +
                        "WHERE c.start_date > CURRENT_DATE " +
                        "  AND c.campaign_status = 1", // 캠페인 상태 필터 추가
                        nativeQuery = true)
        List<SearchResultDTO> findUpcomingCampaigns(@Param("userLoginId") Long userLoginId);

        // ④ 판매(제품) 조회 (status = 0)
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
                        "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.product_id = p.product_id) AS SIGNED) AS likes, "
                        +
                        "  (SELECT GROUP_CONCAT(t.content) FROM ProductTags pt " +
                        "         JOIN Tags t ON pt.tag_id = t.tag_id " +
                        "         WHERE pt.product_id = p.product_id) AS tags, " +
                        "  (CASE WHEN :userLoginId IS NULL THEN 0 ELSE (EXISTS (SELECT 1 FROM Likes l " +
                        "         WHERE l.product_id = p.product_id AND l.user_id = :userLoginId) + 0) END) AS isLiked "
                        +
                        "FROM Products p " +
                        "INNER JOIN Items i ON i.item_id = p.item_id " +
                        "WHERE p.status = 1", // 제품 상태 필터 추가
                        nativeQuery = true)
        List<SearchResultDTO> findSales(@Param("userLoginId") Long userLoginId);

        /**
         * 캠페인 좋아요 많은 순 상위 10개 조회 (campaign_status = 1)
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
                        "  CAST(IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id), 0) AS SIGNED) AS donatedQuantity, "
                        +
                        "  IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id) * 100.0 / "
                        +
                        "         NULLIF((SELECT SUM(cg.quantity_required) FROM CampaignGoals cg WHERE cg.campaign_id = c.campaign_id), 0), 0) AS donationPercentage, "
                        +
                        "  c.created_date AS createdDate, " +
                        "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) AS SIGNED) AS likes, "
                        +
                        "  (SELECT GROUP_CONCAT(t.content) FROM CampaignTags ct " +
                        "         JOIN Tags t ON ct.tag_id = t.tag_id " +
                        "         WHERE ct.campaign_id = c.campaign_id) AS tags, " +
                        "  (CASE WHEN :userLoginId IS NULL THEN 0 ELSE (EXISTS (SELECT 1 FROM Likes l " +
                        "         WHERE l.campaign_id = c.campaign_id AND l.user_id = :userLoginId) + 0) END) AS isLiked "
                        +
                        "FROM Campaigns c " +
                        "WHERE c.campaign_status = 1 " + // 캠페인 상태 필터 추가
                        "ORDER BY (SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) DESC " +
                        "LIMIT 10", nativeQuery = true)
        List<SearchResultDTO> findTop10CampaignsByLikes_add(@Param("userLoginId") Long userLoginId);

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
                "WHERE c.campaign_status = 1 " +
                "  AND c.start_date <= CURRENT_DATE " +
                "  AND c.end_date >= CURRENT_DATE " +
                "ORDER BY (SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) DESC " +
                "LIMIT 10", nativeQuery = true)
        List<SearchResultDTO> findTop10CampaignsByLikes(@Param("userLoginId") Long userLoginId);


        /**
         * 제품 좋아요 많은 순 상위 10개 조회 (status = 0)
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
                        "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.product_id = p.product_id) AS SIGNED) AS likes, "
                        +
                        "  (SELECT GROUP_CONCAT(t.content) FROM ProductTags pt " +
                        "         JOIN Tags t ON pt.tag_id = t.tag_id " +
                        "         WHERE pt.product_id = p.product_id) AS tags, " +
                        "  (CASE WHEN :userLoginId IS NULL THEN 0 ELSE (EXISTS (SELECT 1 FROM Likes l " +
                        "         WHERE l.product_id = p.product_id AND l.user_id = :userLoginId) + 0) END) AS isLiked "
                        +
                        "FROM Products p " +
                        "INNER JOIN Items i ON i.item_id = p.item_id " +
                        "WHERE p.status = 0 " + // 제품 상태 필터 추가
                        "ORDER BY (SELECT COUNT(*) FROM Likes l WHERE l.product_id = p.product_id) DESC " +
                        "LIMIT 10", nativeQuery = true)
        List<SearchResultDTO> findTop10ProductsByLikes_add(@Param("userLoginId") Long userLoginId);

        // 창작가 페이지 진행 중인 캠페인 by Ikjun
        @Query(value = "SELECT " +
                        "  CAST(c.campaign_id AS SIGNED) AS originalId, " +
                        "  'campaign' AS type, " +
                        "  (SELECT iu.image_id FROM ImageUrls iu WHERE iu.photo_id = c.photo_id) AS imageId, " +
                        "  (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = c.created_by) AS sellerName, " +
                        "  c.title AS title, " +
                        "  c.description AS description, " +
                        "  NULL AS price, " +
                        "  CAST(DATEDIFF(c.end_date, CURRENT_DATE) AS SIGNED) AS remainingDays, " +
                        "  CAST(IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id), 0) AS SIGNED) AS donatedQuantity, "
                        +
                        "  IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id) * 100.0 / "
                        +
                        "         NULLIF((SELECT SUM(cg.quantity_required) FROM CampaignGoals cg WHERE cg.campaign_id = c.campaign_id), 0), 0) AS donationPercentage, "
                        +
                        "  c.created_date AS createdDate, " +
                        "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) AS SIGNED) AS likes, "
                        +
                        "  (SELECT GROUP_CONCAT(t.content) FROM CampaignTags ct " +
                        "         JOIN Tags t ON ct.tag_id = t.tag_id " +
                        "         WHERE ct.campaign_id = c.campaign_id) AS tags, " +
                        "  FALSE AS isLiked " + // 좋아요 여부는 일단 false로 처리
                        "FROM Campaigns c " +
                        "WHERE c.created_by = :creatorId " +
                        "  AND c.campaign_status = 1 " + // 진행 중 상태 필터
                        "  AND c.start_date <= CURRENT_DATE " +
                        "  AND c.end_date >= CURRENT_DATE", nativeQuery = true)
        List<SearchResultDTO> findActiveCampaignsByCreatorId(@Param("creatorId") Long creatorId);

        // 창작가 페이지 종료된 캠페인
        @Query(value = "SELECT " +
                        "  CAST(c.campaign_id AS SIGNED) AS originalId, " +
                        "  'campaign' AS type, " +
                        "  (SELECT iu.image_id FROM ImageUrls iu WHERE iu.photo_id = c.photo_id) AS imageId, " +
                        "  (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = c.created_by) AS sellerName, " +
                        "  c.title AS title, " +
                        "  c.description AS description, " +
                        "  NULL AS price, " +
                        "  CAST(DATEDIFF(c.end_date, CURRENT_DATE) AS SIGNED) AS remainingDays, " +
                        "  CAST(IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id), 0) AS SIGNED) AS donatedQuantity, "
                        +
                        "  IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id) * 100.0 / "
                        +
                        "         NULLIF((SELECT SUM(cg.quantity_required) FROM CampaignGoals cg WHERE cg.campaign_id = c.campaign_id), 0), 0) AS donationPercentage, "
                        +
                        "  c.created_date AS createdDate, " +
                        "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) AS SIGNED) AS likes, "
                        +
                        "  (SELECT GROUP_CONCAT(t.content) FROM CampaignTags ct " +
                        "         JOIN Tags t ON ct.tag_id = t.tag_id " +
                        "         WHERE ct.campaign_id = c.campaign_id) AS tags, " +
                        "  FALSE AS isLiked " + // 좋아요 여부는 일단 false로 처리
                        "FROM Campaigns c " +
                        "WHERE c.created_by = :creatorId " +
                        "  AND c.end_date < CURRENT_DATE " + // ✅ 종료된 캠페인 조건
                        "  AND c.campaign_status = 1", // ✅ 상태가 1인 것만 (진행/종료)
                        nativeQuery = true)
        List<SearchResultDTO> findFinishedCampaignsByCreatorId(@Param("creatorId") Long creatorId);

        // 창작가 페이지 예정된 캠페인 by Ikjun
        @Query(value = "SELECT " +
                        "  CAST(c.campaign_id AS SIGNED) AS originalId, " +
                        "  'campaign' AS type, " +
                        "  (SELECT iu.image_id FROM ImageUrls iu WHERE iu.photo_id = c.photo_id) AS imageId, " +
                        "  (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = c.created_by) AS sellerName, " +
                        "  c.title AS title, " +
                        "  c.description AS description, " +
                        "  NULL AS price, " +
                        "  CAST(DATEDIFF(c.end_date, CURRENT_DATE) AS SIGNED) AS remainingDays, " +
                        "  CAST(IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id), 0) AS SIGNED) AS donatedQuantity, "
                        +
                        "  IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id) * 100.0 / "
                        +
                        "         NULLIF((SELECT SUM(cg.quantity_required) FROM CampaignGoals cg WHERE cg.campaign_id = c.campaign_id), 0), 0) AS donationPercentage, "
                        +
                        "  c.created_date AS createdDate, " +
                        "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) AS SIGNED) AS likes, "
                        +
                        "  (SELECT GROUP_CONCAT(t.content) FROM CampaignTags ct " +
                        "         JOIN Tags t ON ct.tag_id = t.tag_id " +
                        "         WHERE ct.campaign_id = c.campaign_id) AS tags, " +
                        "  FALSE AS isLiked " + // 좋아요 여부는 기본값으로 false 설정
                        "FROM Campaigns c " +
                        "WHERE c.created_by = :creatorId " +
                        "  AND c.campaign_status = 1 " + // ✅ 예정 상태 필터 (관리자 승인 완료)
                        "  AND c.start_date > CURRENT_DATE " + // ✅ 오늘 이후에 시작 예정
                        "ORDER BY c.start_date ASC", // ✅ 시작 날짜 오름차순 정렬
                        nativeQuery = true)
        List<SearchResultDTO> findAppointedCampaignsByCreatorId(@Param("creatorId") Long creatorId);

        /**
         * 제품 좋아요 많은 순 상위 10개 조회 (status = 0)
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
                        "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.product_id = p.product_id) AS SIGNED) AS likes, "
                        +
                        "  (SELECT GROUP_CONCAT(t.content) FROM ProductTags pt " +
                        "         JOIN Tags t ON pt.tag_id = t.tag_id " +
                        "         WHERE pt.product_id = p.product_id) AS tags, " +
                        "  (CASE WHEN :userLoginId IS NULL THEN 0 ELSE (EXISTS (SELECT 1 FROM Likes l " +
                        "         WHERE l.product_id = p.product_id AND l.user_id = :userLoginId) + 0) END) AS isLiked "
                        +
                        "FROM Products p " +
                        "INNER JOIN Items i ON i.item_id = p.item_id " +
                        "WHERE p.status = 1 " + // 제품 상태 필터 추가
                        "ORDER BY (SELECT COUNT(*) FROM Likes l WHERE l.product_id = p.product_id) DESC " +
                        "LIMIT 10", nativeQuery = true)
        List<SearchResultDTO> findTop10ProductsByLikes(@Param("userLoginId") Long userLoginId);
}
