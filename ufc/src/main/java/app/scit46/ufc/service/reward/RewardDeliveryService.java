package app.scit46.ufc.service.reward;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.reward.RewardDeliveryDTO;
import app.scit46.ufc.entity.reward.RewardDeliveryEntity;
import app.scit46.ufc.repository.RewardRepository;
import app.scit46.ufc.repository.reward.RewardDeliveryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RewardDeliveryService {

    private final RewardDeliveryRepository rewardDeliveryRepository;
    private final RewardRepository rewardRepository;

    /**
     * 성공한 캠페인의 리워드 배송 데이터 조회
     *
     * @param campaignIds 캠페인 ID 목록
     * @return 리워드 배송 데이터 (리워드 이름, 수량 포함)
     */
    public Map<String, Object> getRewardDeliveryData(List<Long> campaignIds) {
        List<RewardDeliveryEntity> deliveries = rewardDeliveryRepository.findByCampaignIdIn(campaignIds);

        // ✅ rewardName을 추가하고 연락처, 주소, 발송 마감일 포함하여 DTO 변환
        List<Map<String, Object>> deliveryDTOs = deliveries.stream().map(delivery -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("rdeliveryId", delivery.getRDeliveryId());
            dto.put("rewardId", delivery.getReward().getRewardId());
            dto.put("rewardName", Optional.ofNullable(
                    rewardRepository.findRewardNameById(delivery.getReward().getRewardId()))
                    .orElse("Unknown Reward")); // 리워드 이름 null 방지
            dto.put("amount", delivery.getAmount());
            dto.put("status", delivery.getStatus());
            dto.put("invoice", delivery.getInvoice());

            // ✅ 순환 참조 방지를 위한 donation 데이터 변환
            Map<String, Object> donationDto = new HashMap<>();
            donationDto.put("donationId", delivery.getDonation().getDonationId());
            donationDto.put("campaignTitle", delivery.getDonation().getCampaign().getTitle());
            donationDto.put("userName", delivery.getDonation().getUser().getUserName());
            donationDto.put("donorPhone", delivery.getDonation().getUser().getPhoneNumber()); // 기부자 연락처
            donationDto.put("donorAddress", delivery.getDonation().getUser().getUserAddress()); // 기부자 주소
            donationDto.put("dueDate", delivery.getDonation().getCampaign().getSendDate()); // 발송 마감일

            dto.put("donation", donationDto);

            return dto;
        }).toList();

        Map<String, Long> deliveryCounts = Map.of(
                "pending", deliveries.stream().filter(d -> "preparing".equals(d.getStatus())).count(),
                "shipping", deliveries.stream().filter(d -> "shipping".equals(d.getStatus())).count(),
                "completed", deliveries.stream().filter(d -> "completed".equals(d.getStatus())).count(),
                "cancelled", deliveries.stream().filter(d -> "cancelled".equals(d.getStatus())).count());

        return Map.of(
                "rewardDeliveries", deliveryDTOs,
                "deliveryCounts", deliveryCounts);
    }

    public Page<Object[]> getRewardDeliveriesByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return rewardDeliveryRepository.findRewardDeliveriesByUserId(userId, pageable);
    }

    public String getRewardNameByDonationId(Long donationId) {
        return rewardDeliveryRepository.findRewardNameByDonationId(donationId);
    }

    public RewardDeliveryDTO getRewardDeliveryByDonationId(Long donationId) {
        return RewardDeliveryDTO.toDTO(rewardDeliveryRepository.findRewardDeliveryByDonationId(donationId));
    }
}
