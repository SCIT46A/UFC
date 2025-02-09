package app.scit46.ufc.repository;

import app.scit46.ufc.entity.CampaignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<CampaignEntity, Long> {
    @Query("SELECT c FROM CampaignEntity c WHERE REPLACE(c.title, ' ', '') LIKE %:normalizedKeyword%")
    List<CampaignEntity> searchCampaignByTitleIgnoreSpace(@Param("normalizedKeyword") String normalizedKeyword);
}
