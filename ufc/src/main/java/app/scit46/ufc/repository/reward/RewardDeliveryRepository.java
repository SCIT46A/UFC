package app.scit46.ufc.repository.reward;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.scit46.ufc.entity.reward.RewardDeliveryEntity;

public interface RewardDeliveryRepository extends JpaRepository<RewardDeliveryEntity, Long> {

    @Query("SELECT r FROM RewardDeliveryEntity r WHERE r.reward.campaign.campaignId IN :campaignIds")
    List<RewardDeliveryEntity> findByCampaignIdIn(@Param("campaignIds") List<Long> campaignIds);

    @Query("SELECT rw.reward.name, md.donatedDate, c.title, md.quantity, c.photo.imageId, c.isSuccess " +
           "FROM RewardDelivery rw " +
           "JOIN rw.donation md " +
           "JOIN md.campaign c " +
           "JOIN md.user u " +
           "WHERE u.userId = :userId")
    Page<Object[]> findRewardDeliveriesByUserId(@Param("userId") Long userId, Pageable pageable);
}
