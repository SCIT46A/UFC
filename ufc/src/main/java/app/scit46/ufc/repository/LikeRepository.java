package app.scit46.ufc.repository;

import app.scit46.ufc.entity.CampaignEntity;
import app.scit46.ufc.entity.LikeEntity;
import app.scit46.ufc.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    int countByCampaign(CampaignEntity campaign);  // ✅ 올바른 엔티티 관계로 조회
    int countByProduct(ProductEntity product);  // ✅ ProductEntity도 동일하게 적용
}
