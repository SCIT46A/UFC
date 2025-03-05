package app.scit46.ufc.repository.campaign;

import app.scit46.ufc.entity.campaign.CampaignReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignReviewRepository extends JpaRepository<CampaignReviewEntity, Long> {
    List<CampaignReviewEntity> findAllByCampaignedBy_CampaignId(Long campaignId);

    List<CampaignReviewEntity> findByReviewedBy_UserId(Long userId);

    long countByReviewedBy_UserId(Long userId);

    CampaignReviewEntity findBycReviewId(Long cReviewId);

    CampaignReviewEntity findBycReviewIdAndContent(Long cReviewId, String content);
    List<CampaignReviewEntity> findByContentLike(String content);
    Page<CampaignReviewEntity> findAll(Pageable pageable);
    Page<CampaignReviewEntity> findByReviewedBy_UserId(Long userId, Pageable pageable);
}
