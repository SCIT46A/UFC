package app.scit46.ufc.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.scit46.ufc.entity.MaterialDonationEntity;
import app.scit46.ufc.entity.UserEntity;

@Repository
public interface MaterialDonationRepository extends JpaRepository<MaterialDonationEntity, Long> {

    List<MaterialDonationEntity> findByUser(UserEntity user);

    // CampaignEntity의 ID로 MaterialDonationEntity를 찾는 메서드 추가
    List<MaterialDonationEntity> findByCampaign_CampaignId(Long campaignId);

    List<MaterialDonationEntity> findByCampaign_CampaignIdIn(List<Long> campaignIds);

    /**
     * 🔹 특정 캠페인 ID 목록에 해당하는 기부 내역 조회 (최대 limit 개)
     */
    @Query("SELECT d FROM MaterialDonationEntity d WHERE d.campaign.campaignId IN :campaignIds ORDER BY d.donatedDate DESC")
    List<MaterialDonationEntity> findByCampaign_CampaignIdIn(@Param("campaignIds") List<Long> campaignIds,
            Pageable pageable);

    @Query("SELECT d FROM MaterialDonationEntity d JOIN FETCH d.campaign")
    List<MaterialDonationEntity> findAllWithCampaign();

}