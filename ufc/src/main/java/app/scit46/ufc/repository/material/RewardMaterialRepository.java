package app.scit46.ufc.repository.material;

import org.springframework.data.jpa.repository.JpaRepository;

import app.scit46.ufc.entity.reward.RewardMaterialEntity;

public interface RewardMaterialRepository extends JpaRepository<RewardMaterialEntity, Long> {
    
}
