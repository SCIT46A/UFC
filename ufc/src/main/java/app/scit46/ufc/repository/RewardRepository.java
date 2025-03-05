package app.scit46.ufc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.scit46.ufc.entity.reward.RewardEntity;

@Repository
public interface RewardRepository extends JpaRepository<RewardEntity, Long> {

    List<RewardEntity> findAllByCampaign_CampaignId(Long campaignId);

    @Query("SELECT r.rewardName FROM RewardEntity r WHERE r.rewardId = :rewardId")
    String findRewardNameById(@Param("rewardId") Long rewardId);
}
