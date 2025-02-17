package app.scit46.ufc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.scit46.ufc.entity.CampaignEntity;
import app.scit46.ufc.entity.MaterialDonationEntity;

@Repository
public interface CampaignRepository extends JpaRepository<CampaignEntity, Long> {
    
    Optional<CampaignEntity> findByDonation(MaterialDonationEntity materialDonation);
    
}
