package app.scit46.ufc.repository;

import java.util.List;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.scit46.ufc.entity.LikeEntity;
import app.scit46.ufc.entity.campaign.CampaignEntity;
import app.scit46.ufc.entity.product.ProductEntity;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {

    int countByCampaign(CampaignEntity campaign);  // ✅ 올바른 엔티티 관계로 조회

    int countByProduct(ProductEntity product); // ✅ ProductEntity도 동일하게 적용

    List<LikeEntity> findByUser_UserId(Long userId);

    List<LikeEntity> findByCampaign_CampaignId(Long campaignId);

    List<LikeEntity> findByCreator_CreatorId(Long creatorId);

    List<LikeEntity> findByProduct_ProductId(Long productId);
  
    LikeEntity findByUserAndCampaign(UserEntity user, CampaignEntity campaign);
  
    LikeEntity findByUserAndProduct(UserEntity user, ProductEntity product);
  
    LikeEntity findByUserAndCreator(UserEntity user, CreatorEntity creator);

}
