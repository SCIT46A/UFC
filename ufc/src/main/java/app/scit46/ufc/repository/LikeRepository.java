package app.scit46.ufc.repository;

import app.scit46.ufc.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.scit46.ufc.entity.LikeEntity;
import app.scit46.ufc.entity.campaign.CampaignEntity;
import app.scit46.ufc.entity.product.ProductEntity;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    LikeEntity findByUserAndCampaign(UserEntity user, CampaignEntity campaign);
    LikeEntity findByUserAndProduct(UserEntity user, ProductEntity product);
}
