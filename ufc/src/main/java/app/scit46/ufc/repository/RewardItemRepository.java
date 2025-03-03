package app.scit46.ufc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import app.scit46.ufc.entity.reward.RewardItemEntity;

public interface RewardItemRepository extends JpaRepository<RewardItemEntity, Long> {
    
}
