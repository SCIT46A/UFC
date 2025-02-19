package app.scit46.ufc.repository.tag;

import org.springframework.data.jpa.repository.JpaRepository;

import app.scit46.ufc.entity.CampaignTagEntity;

public interface CampaignTagRepository extends JpaRepository<CampaignTagEntity, Long> {
    
}
