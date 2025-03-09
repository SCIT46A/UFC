package app.scit46.ufc.repository.reward;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.scit46.ufc.entity.DonationRewardSelectionEntity;

public interface DonationRewardSelectionRepository extends JpaRepository<DonationRewardSelectionEntity, Long> {
    @Query("SELECT drs FROM DonationRewardSelectionEntity drs " +
           "JOIN FETCH drs.donation d " +
           "JOIN FETCH d.campaign c " +
           "JOIN FETCH drs.reward r " +
           "LEFT JOIN FETCH r.rewardItems ri " +
           "WHERE d.user.userId = :userId")
    List<DonationRewardSelectionEntity> findByDonationUserId(@Param("userId") Long userId);
}   
