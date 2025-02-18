package app.scit46.ufc.repository;

import app.scit46.ufc.dto.SearchResultDTO;
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

    // ① 전체 검색 (캠페인 + 제품): 검색어 필터링
    @Query(value = "SELECT * FROM ( " +
            "  SELECT " +
            "    CAST(c.campaign_id AS SIGNED) AS originalId, " +
            "    'campaign' AS type, " +
            "    CAST(c.photo_id AS SIGNED) AS imageId, " +
            "    (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = c.created_by) AS sellerName, " +
            "    c.title AS title, " +
            "    (SELECT cb.title FROM CampaignBoards cb WHERE cb.campaign_id = c.campaign_id " +
            "         ORDER BY cb.created_date DESC LIMIT 1) AS description, " +
            "    NULL AS price, " +
            "    CAST(DATEDIFF(c.end_date, CURRENT_DATE) AS SIGNED) AS remainingDays, " +
            "    CAST(IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md " +
            "                   WHERE md.campaign_id = c.campaign_id), 0) AS SIGNED) AS donatedQuantity, " +
            "    IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md " +
            "            WHERE md.campaign_id = c.campaign_id) * 100.0 / " +
            "           NULLIF((SELECT SUM(cg.quantity_required) FROM CampaignGoals cg " +
            "                   WHERE cg.campaign_id = c.campaign_id), 0), 0) AS donationPercentage, " +
            "    c.created_date AS createdDate, " +
            "    CAST((SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) AS SIGNED) AS likes, " +
            "    (SELECT GROUP_CONCAT(t.content) FROM CampaignTags ct " +
            "         JOIN Tags t ON ct.tag_id = t.tag_id " +
            "         WHERE ct.campaign_id = c.campaign_id) AS tags " +
            "  FROM Campaigns c " +
            "  WHERE c.title LIKE CONCAT('%', :keyword, '%') " +
            "  UNION " +
            "  SELECT " +
            "    CAST(p.product_id AS SIGNED) AS originalId, " +
            "    'product' AS type, " +
            "    CAST(i.photo_id AS SIGNED) AS imageId, " +
            "    (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = p.created_by) AS sellerName, " +
            "    i.name AS title, " +
            "    i.description AS description, " +
            "    i.price AS price, " +
            "    NULL AS remainingDays, " +
            "    NULL AS donatedQuantity, " +
            "    NULL AS donationPercentage, " +
            "    NULL AS createdDate, " +
            "    CAST((SELECT COUNT(*) FROM Likes l WHERE l.product_id = p.product_id) AS SIGNED) AS likes, " +
                    "    (SELECT GROUP_CONCAT(t.content) FROM ProductTags pt " +
                    "         JOIN Tags t ON pt.tag_id = t.tag_id " +
                    "         WHERE pt.product_id = p.product_id) AS tags " +
                    "  FROM Products p " +
                    "  INNER JOIN Items i ON i.item_id = p.item_id " +
                    "  WHERE i.name LIKE CONCAT('%', :keyword, '%') " +
                    ") AS results",
    nativeQuery = true)
    List<SearchResultDTO> findSearchResults(@Param("keyword") String keyword);

    // ② 진행 중인 캠페인: 검색어 없이 전체 데이터를 불러옴
    @Query(value = "SELECT " +
            "  CAST(c.campaign_id AS SIGNED) AS originalId, " +
            "  'campaign' AS type, " +
            "  CAST(c.photo_id AS SIGNED) AS imageId, " +
            "  (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = c.created_by) AS sellerName, " +
            "  c.title AS title, " +
            "  (SELECT cb.title FROM CampaignBoards cb WHERE cb.campaign_id = c.campaign_id " +
            "         ORDER BY cb.created_date DESC LIMIT 1) AS description, " +
            "  NULL AS price, " +
            "  CAST(DATEDIFF(c.end_date, CURRENT_DATE) AS SIGNED) AS remainingDays, " +
            "  CAST(IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id), 0) AS SIGNED) AS donatedQuantity, " +
            "  IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id) * 100.0 / " +
            "         NULLIF((SELECT SUM(cg.quantity_required) FROM CampaignGoals cg WHERE cg.campaign_id = c.campaign_id), 0), 0) AS donationPercentage, " +
            "  c.created_date AS createdDate, " +
            "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) AS SIGNED) AS likes, " +
            "  (SELECT GROUP_CONCAT(t.content) FROM CampaignTags ct " +
            "         JOIN Tags t ON ct.tag_id = t.tag_id " +
            "         WHERE ct.campaign_id = c.campaign_id) AS tags " +
            "FROM Campaigns c " +
            "WHERE c.start_date <= CURRENT_DATE " +
            "  AND c.end_date >= CURRENT_DATE",
            nativeQuery = true)
    List<SearchResultDTO> findOngoingCampaigns();

    // ③ 진행 예정인 캠페인: 검색어 없이 전체 데이터를 불러옴
    @Query(value = "SELECT " +
            "  CAST(c.campaign_id AS SIGNED) AS originalId, " +
            "  'campaign' AS type, " +
            "  CAST(c.photo_id AS SIGNED) AS imageId, " +
            "  (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = c.created_by) AS sellerName, " +
            "  c.title AS title, " +
            "  (SELECT cb.title FROM CampaignBoards cb WHERE cb.campaign_id = c.campaign_id " +
            "         ORDER BY cb.created_date DESC LIMIT 1) AS description, " +
            "  NULL AS price, " +
            "  CAST(DATEDIFF(c.end_date, CURRENT_DATE) AS SIGNED) AS remainingDays, " +
            "  CAST(IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id), 0) AS SIGNED) AS donatedQuantity, " +
            "  IFNULL((SELECT SUM(md.quantity) FROM MaterialsDonations md WHERE md.campaign_id = c.campaign_id) * 100.0 / " +
            "         NULLIF((SELECT SUM(cg.quantity_required) FROM CampaignGoals cg WHERE cg.campaign_id = c.campaign_id), 0), 0) AS donationPercentage, " +
            "  c.created_date AS createdDate, " +
            "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.campaign_id = c.campaign_id) AS SIGNED) AS likes, " +
            "  (SELECT GROUP_CONCAT(t.content) FROM CampaignTags ct " +
            "         JOIN Tags t ON ct.tag_id = t.tag_id " +
            "         WHERE ct.campaign_id = c.campaign_id) AS tags " +
            "FROM Campaigns c " +
            "WHERE c.start_date > CURRENT_DATE",
            nativeQuery = true)
    List<SearchResultDTO> findUpcomingCampaigns();

    // ④ 판매 (제품) 조회: 검색어 없이 전체 데이터를 불러옴
    @Query(value = "SELECT " +
            "  CAST(p.product_id AS SIGNED) AS originalId, " +
            "  'product' AS type, " +
            "  CAST(i.photo_id AS SIGNED) AS imageId, " +
            "  (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = p.created_by) AS sellerName, " +
            "  i.name AS title, " +
            "  i.description AS description, " +
            "  i.price AS price, " +
            "  NULL AS remainingDays, " +
            "  NULL AS donatedQuantity, " +
            "  NULL AS donationPercentage, " +
            "  NULL AS createdDate, " +
            "  CAST((SELECT COUNT(*) FROM Likes l WHERE l.product_id = p.product_id) AS SIGNED) AS likes, " +
                    "  (SELECT GROUP_CONCAT(t.content) FROM ProductTags pt " +
                    "         JOIN Tags t ON pt.tag_id = t.tag_id " +
                    "         WHERE pt.product_id = p.product_id) AS tags " +
                    "FROM Products p " +
                    "INNER JOIN Items i ON i.item_id = p.item_id",
    nativeQuery = true)
    List<SearchResultDTO> findSales();
}
