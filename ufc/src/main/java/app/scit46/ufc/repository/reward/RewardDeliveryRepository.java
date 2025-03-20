package app.scit46.ufc.repository.reward;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.scit46.ufc.entity.reward.RewardDeliveryEntity;
import java.util.Optional;

public interface RewardDeliveryRepository extends JpaRepository<RewardDeliveryEntity, Long> {

    //
    @Query("SELECT r FROM RewardDeliveryEntity r WHERE r.reward.campaign.campaignId IN :campaignIds")
    List<RewardDeliveryEntity> findByCampaignIdIn(@Param("campaignIds") List<Long> campaignIds);


    Optional<RewardDeliveryEntity> findByDonation_DonationId(Long donationId);

   @Query("SELECT rw.reward.rewardName, md.donatedDate, c.title, md.quantity, c.photo.imageId, c.isSuccess " +
       "FROM RewardDeliveryEntity rw " +
       "JOIN rw.donation md " +
       "JOIN md.campaign c " +
       "WHERE md.user.userId = :userId")
   Page<Object[]> findRewardDeliveriesByUserId(@Param("userId") Long userId, Pageable pageable);

   @Query("SELECT rw.reward.rewardName FROM RewardDeliveryEntity rw WHERE rw.donation.donationId = :donationId")
   String findRewardNameByDonationId(@Param("donationId") Long donationId);

   @Query("SELECT rw FROM RewardDeliveryEntity rw WHERE rw.donation.donationId = :donationId")
   RewardDeliveryEntity findRewardDeliveryByDonationId(@Param("donationId") Long donationId);

    @Query("SELECT r.reward.rewardName FROM RewardDeliveryEntity r WHERE r.donation.id = :donationId")
    List<String> findRewardNamesByDonationId(@Param("donationId") Long donationId);
    
   @Query("SELECT rd FROM RewardDeliveryEntity rd WHERE rd.donation.id IN :donationIds")
   List<RewardDeliveryEntity> findByDonationIdIn(@Param("donationIds") List<Long> donationIds);

    
    

}
