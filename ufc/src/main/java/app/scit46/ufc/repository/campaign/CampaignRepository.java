package app.scit46.ufc.repository.campaign;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.custom.IntroPageCampaignDTO;
import app.scit46.ufc.entity.campaign.CampaignEntity;

public interface CampaignRepository extends JpaRepository<CampaignEntity, Long> {

    List<CampaignEntity> findByCampaignId(Long campaignId);
    List<CampaignEntity> findByCampaignIdIn(List<Long> campaignId);

    // ✅ 승인 대기 중인 캠페인 조회 (campaign_status = false)
    @Query("SELECT c FROM CampaignEntity c WHERE c.campaignStatus = 0")
    List<CampaignEntity> findByPendingApproval();

    @Query("SELECT c FROM CampaignEntity c LEFT JOIN FETCH c.createdBy")
    List<CampaignEntity> findAllWithCreator();


    // ✅ 특정 캠페인 상태(승인된)이며 시작일이 현재보다 이후인 캠페인 조회
    List<CampaignEntity> findByCampaignStatusAndStartDateAfter(Integer  campaignStatus, LocalDateTime startDate);

    List<CampaignEntity> findByTitleContaining(String title);



    // ✅ 창작자가 만든 캠페인 조회 (Creator Dashboard)
    List<CampaignEntity> findByCreatedBy_CreatorId(Long creatorId);

    // ✅ 창작자가 성공한 캠페인 ID 조회 (Creator Dashboard)
    @Query("SELECT c.campaignId FROM CampaignEntity c WHERE c.createdBy.id = :creatorId AND c.isSuccess = true")
    List<Long> findSuccessfulCampaignIdsByCreator(@Param("creatorId") Long creatorId);

    @Query(value = "SELECT " +
            "  c.campaign_id AS campaignId, " +
            "  'campaign' AS type, " +
            "  (SELECT iu.image_id FROM ImageUrls iu WHERE iu.photo_id = c.photo_id) AS imageId, " +
            "  (SELECT cr.b_name FROM Creators cr WHERE cr.creator_id = c.created_by) AS sellerName, " +
            "  c.title AS campaignTitle, " +
            "  c.description AS campaignDescription, " +
            "  cg.goal_id AS goalId, " +
            "  (SELECT m.name FROM Materials m WHERE m.material_id = cg.material_id) AS goalTitle, " +
            "  cg.quantity_required AS requiredQuantity, " +
            "  IFNULL(SUM(md.quantity), 0) AS donatedQuantity, " +
            "  IFNULL(SUM(md.quantity) * 100.0 / NULLIF(cg.quantity_required, 0), 0) AS donationPercentage, " +
            "  COUNT(DISTINCT md.user_id) AS totalDonors, " +
            "  (SELECT COUNT(DISTINCT md2.user_id) FROM MaterialsDonations md2 WHERE md2.campaign_id = c.campaign_id) AS campaignDonors " +
            "FROM Campaigns c " +
            "LEFT JOIN CampaignGoals cg ON c.campaign_id = cg.campaign_id " +
            "LEFT JOIN MaterialsDonations md ON md.campaign_id = c.campaign_id AND md.material_id = cg.material_id " +
            "WHERE c.start_date <= CURRENT_DATE " +
            "  AND c.end_date >= CURRENT_DATE " +
            "  AND c.campaign_status = 1 " +
            "GROUP BY c.campaign_id, cg.goal_id " +
            "ORDER BY donationPercentage ASC ", nativeQuery = true)
    List<IntroPageCampaignDTO> findCampaignGoalRows();

    @Query("SELECT c FROM LikeEntity l JOIN l.campaign c WHERE l.user.id = :userId")
    Page<CampaignEntity> findLikedCampaignsByUserId(@Param("userId") Long userId, Pageable pageable);   

    Page<CampaignEntity> findByCampaignId(Long campaignId, Pageable pageable);
    
    @Query(value = "SELECT " +
        "CASE WHEN IFNULL(SUM(md.quantity) * 100.0 / NULLIF(cg.quantity_required, 0), 0) >= 100 " +
        "THEN 1 ELSE 0 END AS isAchieved " +
        "FROM Campaigns c " +
        "LEFT JOIN CampaignGoals cg ON c.campaign_id = cg.campaign_id " +
        "LEFT JOIN MaterialsDonations md ON md.campaign_id = c.campaign_id AND md.material_id = cg.material_id " +
        "WHERE c.campaign_id = :campaignId " +
        "GROUP BY c.campaign_id, cg.goal_id", nativeQuery = true)
    Integer isCampaignAchieved(@Param("campaignId") Long campaignId);

}
