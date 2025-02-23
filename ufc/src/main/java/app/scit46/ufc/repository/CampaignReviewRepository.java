package app.scit46.ufc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.scit46.ufc.entity.CampaignReviewEntity;

public interface CampaignReviewRepository extends JpaRepository<CampaignReviewEntity, Long> {
    List<CampaignReviewEntity> findByReviewedBy_UserId(Long userId);

    long countByReviewedBy_UserId(Long userId);
}
