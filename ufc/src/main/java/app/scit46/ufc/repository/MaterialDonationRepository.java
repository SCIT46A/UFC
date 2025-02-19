package app.scit46.ufc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.scit46.ufc.entity.MaterialDonationEntity;
import app.scit46.ufc.entity.UserEntity;

@Repository
public interface MaterialDonationRepository extends JpaRepository<MaterialDonationEntity, Long> {
    
    List<MaterialDonationEntity> findByUser(UserEntity user);
    
    // CampaignEntity의 ID로 MaterialDonationEntity를 찾는 메서드 추가
    List<MaterialDonationEntity> findByCampaign_CampaignId(Long campaignId);

}
