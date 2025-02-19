package app.scit46.ufc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.scit46.ufc.entity.CampaignEntity;


@Repository
public interface CampaignRepository extends JpaRepository<CampaignEntity, Long> {
    
    List<CampaignEntity> findByCampaignId(Long campaignId);

}
