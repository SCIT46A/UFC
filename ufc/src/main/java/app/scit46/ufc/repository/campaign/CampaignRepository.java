package app.scit46.ufc.repository.campaign;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.scit46.ufc.entity.CampaignEntity;

public interface CampaignRepository extends JpaRepository<CampaignEntity, Long> {

    //List<CampaignEntity> findByTitleContainingOrTagsContaining(String searchKeyword, String searchKeyword2);
    List<CampaignEntity> findByTitleContaining(String searchKeyword);
    
}
