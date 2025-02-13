package app.scit46.ufc.repository;

import app.scit46.ufc.entity.CampaignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<CampaignEntity, Long> {

    // ✅ 승인 대기 중인 캠페인 조회 (campaign_status = false)
    @Query("SELECT c FROM CampaignEntity c JOIN FETCH c.createdBy WHERE c.campaignStatus = false")
    List<CampaignEntity> findByPendingApproval();

    // ✅ 전체 캠페인 조회 (N+1 문제 해결)
    @Query("SELECT c FROM CampaignEntity c JOIN FETCH c.createdBy")
    List<CampaignEntity> findAllWithCreator();
}
