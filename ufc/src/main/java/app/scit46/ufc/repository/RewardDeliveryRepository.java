package app.scit46.ufc.repository;

import app.scit46.ufc.dto.reward.RewardDeliveryDTO;
import app.scit46.ufc.entity.reward.RewardDeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RewardDeliveryRepository extends JpaRepository<RewardDeliveryEntity, Long> {
}
