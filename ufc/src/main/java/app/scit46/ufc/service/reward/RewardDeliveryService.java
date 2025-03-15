package app.scit46.ufc.service.reward;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

import app.scit46.ufc.repository.reward.RewardDeliveryRepository;
import java.util.Optional;
import app.scit46.ufc.repository.RewardRepository;
import app.scit46.ufc.entity.reward.RewardDeliveryEntity;
import app.scit46.ufc.dto.delivery.InvoiceUpdateRequest;
import app.scit46.ufc.dto.reward.RewardDeliveryDTO;
import java.util.HashMap;

import jakarta.transaction.Transactional;
import java.util.stream.Collectors;
import app.scit46.ufc.service.delivery.DeliveryService;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RewardDeliveryService {

        private final RewardDeliveryRepository rewardDeliveryRepository;
        private final RewardRepository rewardRepository;
        private final DeliveryService deliveryService;

        /**
         * 성공한 캠페인의 리워드 배송 데이터 조회
         *
         * @param campaignIds 캠페인 ID 목록
         * @return 리워드 배송 데이터 (리워드 이름, 수량 포함)
         */
        public Map<String, Object> getRewardDeliveryData(List<Long> campaignIds) {
                List<RewardDeliveryEntity> deliveries = rewardDeliveryRepository.findByCampaignIdIn(campaignIds);

                // ✅ 배송 상태 조회 시 null 방지
                Map<String, String> trackingStatuses = deliveryService.trackRewardDeliveries(deliveries);

                List<Map<String, Object>> deliveryDTOs = deliveries.stream().map(delivery -> {
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("rdeliveryId", Optional.ofNullable(delivery.getRDeliveryId()).orElse(0L));
                        dto.put("rewardId", Optional.ofNullable(delivery.getReward().getRewardId()).orElse(0L));
                        dto.put("rewardName", Optional.ofNullable(
                                        rewardRepository.findRewardNameById(delivery.getReward().getRewardId()))
                                        .orElse("Unknown Reward"));
                        dto.put("amount", Optional.ofNullable(delivery.getAmount()).orElse(0));
                        dto.put("status", Optional.ofNullable(delivery.getStatus()).orElse("미등록"));
                        dto.put("invoice", Optional.ofNullable(delivery.getInvoice()).orElse(""));

                        // ✅ `invoice.split("#")`에서 `null` 방지
                        String invoice = Optional.ofNullable(delivery.getInvoice()).orElse("");
                        String[] parts = invoice.split("#");
                        String courierId = parts.length > 0 ? parts[0] : "";
                        String trackingNumber = parts.length > 1 ? parts[1] : "";

                        // 🚀 배송 상태 조회 (null 방지)
                        String trackingStatus = trackingStatuses.getOrDefault(trackingNumber, "미등록");
                        dto.put("deliveryStatus", trackingStatus);

                        // ✅ 기부자 정보 변환 (null 방지)
                        Map<String, Object> donationDto = new HashMap<>();
                        donationDto.put("donationId",
                                        Optional.ofNullable(delivery.getDonation().getDonationId()).orElse(0L));
                        donationDto.put("campaignTitle",
                                        Optional.ofNullable(delivery.getDonation().getCampaign().getTitle())
                                                        .orElse("-"));
                        donationDto.put("userName",
                                        Optional.ofNullable(delivery.getDonation().getUser().getUserName())
                                                        .orElse("미등록"));
                        donationDto.put("donorPhone",
                                        Optional.ofNullable(delivery.getDonation().getUser().getPhoneNumber())
                                                        .orElse("-"));
                        donationDto.put("donorAddress",
                                        Optional.ofNullable(delivery.getDonation().getUser().getUserAddress())
                                                        .orElse("-"));
                        donationDto.put("dueDate",
                                        Optional.ofNullable(delivery.getDonation().getCampaign().getSendDate())
                                                        .orElse(null));

                        dto.put("donation", donationDto);

                        return dto;
                }).toList();

                // ✅ 배송 상태 카운트 맵핑
                Map<String, Long> deliveryCounts = deliveries.stream()
                                .collect(Collectors.groupingBy(
                                                delivery -> Optional.ofNullable(delivery.getStatus()).orElse("미등록"),
                                                Collectors.counting()));

                return Map.of(
                                "rewardDeliveries", deliveryDTOs,
                                "deliveryCounts", deliveryCounts);
        }

        @Transactional
        public void updateInvoices(List<InvoiceUpdateRequest> updateRequests) {
                for (InvoiceUpdateRequest request : updateRequests) {
                        RewardDeliveryEntity delivery = rewardDeliveryRepository
                                        .findByDonation_DonationId(request.getId())
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "기부번호에 해당하는 배송 정보를 찾을 수 없음: " + request.getId()));

                        delivery.setInvoice(request.getInvoice()); // "택배사코드#송장번호"
                        rewardDeliveryRepository.save(delivery);
                }
        }

        @Transactional
        public void updateInvoice(Long rewardDeliveryId, String courier, String trackingNumber) {
                RewardDeliveryEntity delivery = rewardDeliveryRepository.findById(rewardDeliveryId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "🚨 배송 정보를 찾을 수 없음: " + rewardDeliveryId));

                delivery.setInvoice(courier + "#" + trackingNumber);
                rewardDeliveryRepository.save(delivery);
        }

}
