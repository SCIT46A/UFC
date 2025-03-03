package app.scit46.ufc.repository.reward;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.scit46.ufc.entity.reward.RewardEntity;

public interface RewardRepository extends JpaRepository<RewardEntity, Long> {
    // @Query("SELECT r.rewardName FROM RewardEntity r WHERE r.rewardId =
    // :rewardId")
    // String findRewardNameById(@Param("rewardId") Long rewardId);
}
