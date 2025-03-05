package app.scit46.ufc.repository.campaign;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.scit46.ufc.entity.campaign.CampaignEntity;

public interface CampaignRepository extends JpaRepository<CampaignEntity, Long> {

    List<CampaignEntity> findByCampaignId(Long campaignId);

    // ✅ 승인 대기 중인 캠페인 조회 (campaign_status = false)
    @Query("SELECT c FROM CampaignEntity c WHERE c.campaignStatus = 0")
    List<CampaignEntity> findByPendingApproval();

    @Query("SELECT c FROM CampaignEntity c LEFT JOIN FETCH c.createdBy")
    List<CampaignEntity> findAllWithCreator();


    // ✅ 특정 캠페인 상태(승인된)이며 시작일이 현재보다 이후인 캠페인 조회
    List<CampaignEntity> findByCampaignStatusAndStartDateAfter(int campaignStatus, LocalDateTime startDate);

    List<CampaignEntity> findByTitleContaining(String title);

    
}
