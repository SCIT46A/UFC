package app.scit46.ufc.service.reward;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

import app.scit46.ufc.repository.reward.RewardDeliveryRepository;
import app.scit46.ufc.entity.reward.RewardDeliveryEntity;
import app.scit46.ufc.dto.reward.RewardDeliveryDTO;

@Service
@RequiredArgsConstructor
public class RewardDeliveryService {

    private final RewardDeliveryRepository rewardDeliveryRepository;

    /**
     * 성공한 캠페인의 리워드 배송 데이터 조회
     *
     * @param campaignIds 캠페인 ID 목록
     * @return 리워드 배송 데이터 (리워드 이름, 수량 포함)
     */
    public Map<String, Object> getRewardDeliveryData(List<Long> campaignIds) {
        List<RewardDeliveryEntity> deliveries = rewardDeliveryRepository.findByCampaignIdIn(campaignIds);

        List<RewardDeliveryDTO> deliveryDTOs = deliveries.stream().map(RewardDeliveryDTO::toDTO).toList();

        Map<String, Long> deliveryCounts = Map.of(
                "pending", deliveries.stream().filter(d -> "preparing".equals(d.getStatus())).count(),
                "shipping", deliveries.stream().filter(d -> "shipping".equals(d.getStatus())).count(),
                "completed", deliveries.stream().filter(d -> "completed".equals(d.getStatus())).count(),
                "cancelled", deliveries.stream().filter(d -> "cancelled".equals(d.getStatus())).count());

        return Map.of(
                "rewardDeliveries", deliveryDTOs,
                "deliveryCounts", deliveryCounts);
    }
}
