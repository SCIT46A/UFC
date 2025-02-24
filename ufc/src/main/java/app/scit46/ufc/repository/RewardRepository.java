package app.scit46.ufc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import app.scit46.ufc.entity.reward.RewardEntity;

public interface RewardRepository extends JpaRepository<RewardEntity, Long> {
    
}
