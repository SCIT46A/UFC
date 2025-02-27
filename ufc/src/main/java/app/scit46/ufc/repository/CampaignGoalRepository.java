package app.scit46.ufc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import app.scit46.ufc.entity.campaign.CampaignGoalEntity;

@Repository
public interface CampaignGoalRepository extends JpaRepository<CampaignGoalEntity, Long> {

    @Query("SELECT g FROM CampaignGoalEntity g JOIN FETCH g.campaign")
    List<CampaignGoalEntity> findAllWithCampaign();

    // Repository 메소드명만 정확히 맞춰 수정 (추천)
    List<CampaignGoalEntity> findByCampaign_CampaignId(Long campaignId);


}
