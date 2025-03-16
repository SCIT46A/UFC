package app.scit46.ufc.repository;

import app.scit46.ufc.entity.campaign.CampaignEntity;
import app.scit46.ufc.entity.reward.RewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.scit46.ufc.entity.reward.RewardEntity;
import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<RewardEntity, Long> {

    List<RewardEntity> findAllByCampaign_CampaignId(Long campaignId);

    @Query("SELECT r.rewardName FROM RewardEntity r WHERE r.rewardId = :rewardId")
    String findRewardNameById(@Param("rewardId") Long rewardId);

    void deleteByCampaign(CampaignEntity campaignEntity);

    List<RewardEntity> findAllByCampaign(CampaignEntity campaignEntity);
}
