package app.scit46.ufc.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;

import app.scit46.ufc.service.campaign.CampaignService;
import app.scit46.ufc.service.MaterialDonationService;
import app.scit46.ufc.dto.MaterialDonationDTO;
import app.scit46.ufc.service.delivery.CourierService;
import app.scit46.ufc.service.delivery.DeliveryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestParam;
import app.scit46.ufc.dto.reward.RewardDeliveryDTO;
import app.scit46.ufc.service.reward.RewardDeliveryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/creator/dashboard")
@RequiredArgsConstructor
public class ApiCreatorDashboardController {

        private final CampaignService campaignService;
        private final MaterialDonationService materialDonationService;
        private final CourierService courierService;
        private final DeliveryService deliveryService;
        private final MaterialDonationService donationOrderService;
        private final RewardDeliveryService rewardDeliveryService;

        /**
         * 기부 내역 조회
         * 
         * @param session
         * @return
         */
        @GetMapping("/donation/orders")
        public ResponseEntity<Map<String, Object>> getDonationOrders(HttpSession session) {
                // 세션에서 creatorId 가져오기
                Long creatorId = (Long) session.getAttribute("creatorId");
                if (creatorId == null) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Unauthorized"));
                }

                // 해당 창작자의 캠페인 ID 목록 가져오기
                List<Long> campaignIds = campaignService.getCampaignIdsByCreator(creatorId);

                // 캠페인 ID가 없으면 빈 데이터 반환
                if (campaignIds.isEmpty()) {
                        return ResponseEntity.ok(Map.of(
                                        "campaigns", List.of(),
                                        "donations", List.of(),
                                        "donationCounts",
                                        Map.of("pending", 0, "processing", 0, "rejected", 0, "approved", 0) // 빈 개수 반환
                        ));
                }

                // 기부 내역 조회 (creator_id에 해당하는 캠페인만 필터링)
                List<MaterialDonationDTO> donations = materialDonationService.getDonationsByCampaignIds(campaignIds);

                // ✅ 전체 배송 상태 한 번에 조회
                Map<String, String> trackingStatuses = deliveryService.trackMultipleDeliveries(donations);

                // 기부 상태별 개수 계산 (판매자 ID 기준)
                Map<String, Long> donationCounts = donations.stream()
                                .collect(Collectors.groupingBy(MaterialDonationDTO::getStatus, Collectors.counting()));

                // 로그 확인 (필터링된 개수)
                System.out.println("🔹 필터링된 donationCounts: " + donationCounts);

                // ✅ 변환된 기부 데이터 목록 생성 (`courierId` → `courierName`, `trackingStatus` 변환)
                List<Map<String, Object>> transformedDonations = donations.stream().map(donation -> {
                        String courierId = donation.getCourierId();
                        String trackingNumber = donation.getTrackingNumber();

                        Map<String, Object> donationMap = new HashMap<>();
                        donationMap.put("donationId", donation.getDonationId());
                        donationMap.put("campaignTitle", donation.getCampaign().getTitle());
                        donationMap.put("donatedDate", donation.getDonatedDate());
                        donationMap.put("userName", donation.getUser().getUserName());
                        donationMap.put("materialName", donation.getMaterial().getName());
                        donationMap.put("quantity", donation.getQuantity());
                        donationMap.put("trackingNumber", trackingNumber);
                        donationMap.put("courierName", courierService.getCourierNameById(courierId));
                        // ✅ 배송 상태 값 로그 출력 (문제 확인)
                        String trackingStatus = trackingStatuses.getOrDefault(trackingNumber, "미등록");
                        donationMap.put("trackingStatus", trackingStatus);
                        donationMap.put("status", donation.getStatus());
                        return donationMap;
                }).collect(Collectors.toList());

                return ResponseEntity.ok(Map.of(
                                "campaigns", campaignService.findByCampaign_CampaignIdIn(campaignIds),
                                "donations", transformedDonations,
                                "donationCounts", donationCounts // 기부 상태별 개수 추가
                ));
        }

        /**
         * 기부 내역 승인/반려 처리
         * 
         * @param donationId
         * @param action
         * @return
         */
        @PostMapping("/donation/orders/{donationId}/{action}")
        public ResponseEntity<?> updateDonationStatus(
                        @PathVariable("donationId") Long donationId,
                        @PathVariable("action") String action) {

                boolean isApproved = "approved".equalsIgnoreCase(action);

                donationOrderService.updateDonationStatus(donationId, isApproved);

                return ResponseEntity.ok().body(Map.of("message", isApproved ? "승인 완료" : "반려 처리 완료"));
        }

        /**
         * 기부 내역 상태별 개수 조회
         * 
         * @param session
         * @return
         */
        @GetMapping("/donation/orders/counts")
        public ResponseEntity<Map<String, Map<String, Long>>> getDonationCounts(HttpSession session) {
                Long creatorId = (Long) session.getAttribute("creatorId");

                // 현재 창작자의 캠페인 ID 목록 가져오기
                List<Long> campaignIds = campaignService.getCampaignIdsByCreator(creatorId);

                // 해당 캠페인에 대한 기부 내역 가져오기
                List<MaterialDonationDTO> donations = materialDonationService.getDonationsByCampaignIds(campaignIds);

                // 🚀 상태별 개수 계산
                Map<String, Long> donationCounts = donations.stream()
                                .collect(Collectors.groupingBy(MaterialDonationDTO::getStatus, Collectors.counting()));

                // 🚀 누락된 상태를 0으로 채우기
                Map<String, Long> completeCounts = new HashMap<>();
                completeCounts.put("pending", donationCounts.getOrDefault("pending", 0L));
                completeCounts.put("processing", donationCounts.getOrDefault("processing", 0L));
                completeCounts.put("rejected", donationCounts.getOrDefault("rejected", 0L));
                completeCounts.put("approved", donationCounts.getOrDefault("approved", 0L));

                // 🚀 donationCounts 키로 감싸서 반환해야 함!!
                return ResponseEntity.ok(Map.of("donationCounts", completeCounts));
        }

        // /**
        // * ✅ 판매자(창작자)별 리워드 배송 내역 조회
        // */
        // @GetMapping("/reward/deliveries")
        // public ResponseEntity<Map<String, Object>> getRewardDeliveries(HttpSession
        // session,
        // @RequestParam(required = false) String status) {

        // // ✅ 세션에서 creatorId 가져오기
        // Long creatorId = (Long) session.getAttribute("creatorId");
        // if (creatorId == null) {
        // return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error",
        // "Unauthorized"));
        // }

        // // ✅ 창작자의 캠페인 ID 목록 가져오기
        // List<Long> campaignIds = campaignService.getCampaignIdsByCreator(creatorId);

        // if (campaignIds.isEmpty()) {
        // return ResponseEntity.ok(Map.of(
        // "campaigns", List.of(),
        // "deliveries", List.of(),
        // "deliveryCounts",
        // Map.of("preparing", 0, "shipping", 0, "completed", 0, "cancelled", 0)));
        // }

        // // ✅ 리워드 배송 내역 조회 (판매자 ID에 해당하는 캠페인만 필터링)
        // List<RewardDeliveryDTO> deliveries =
        // rewardDeliveryService.findByCampaign_CampaignIdInAndStatus(
        // campaignIds,
        // status);

        // // ✅ 배송 상태별 개수 계산
        // Map<String, Long> deliveryCounts = deliveries.stream()
        // .collect(Collectors.groupingBy(RewardDeliveryDTO::getStatus,
        // Collectors.counting()));

        // return ResponseEntity.ok(Map.of(
        // "campaigns", campaignService.findByCampaign_CampaignIdIn(campaignIds),
        // "deliveries", deliveries,
        // "deliveryCounts", deliveryCounts));
        // }

        // /**
        // * ✅ 리워드 발송 처리 (송장번호 입력)
        // */
        // @PostMapping("/reward/deliveries/{deliveryId}/ship")
        // public ResponseEntity<?> processRewardDelivery(
        // @PathVariable("deliveryId") Long deliveryId,
        // @RequestBody RewardShipmentRequest request,
        // HttpSession session) {

        // Long creatorId = (Long) session.getAttribute("creatorId");
        // if (creatorId == null) {
        // return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error",
        // "Unauthorized"));
        // }

        // boolean success = rewardDeliveryService.processShipment(deliveryId,
        // creatorId, request);
        // if (success) {
        // return ResponseEntity.ok(Map.of("message", "배송 처리가 완료되었습니다."));
        // } else {
        // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "배송
        // 처리 실패"));
        // }
        // }

        // /**
        // * ✅ 리워드 배송 상태별 개수 조회 (대시보드)
        // */
        // @GetMapping("/reward/deliveries/status")
        // public ResponseEntity<Map<String, Map<String, Long>>>
        // getRewardDeliveryStatusCounts(HttpSession session) {
        // Long creatorId = (Long) session.getAttribute("creatorId");

        // List<Long> campaignIds = campaignService.getCampaignIdsByCreator(creatorId);
        // List<RewardDeliveryDTO> deliveries =
        // rewardDeliveryService.getDeliveriesByCampaignIds(campaignIds,
        // null);

        // Map<String, Long> deliveryCounts = deliveries.stream()
        // .collect(Collectors.groupingBy(RewardDeliveryDTO::getStatus,
        // Collectors.counting()));

        // // 🚀 누락된 상태 0으로 채우기
        // Map<String, Long> completeCounts = new HashMap<>();
        // completeCounts.put("preparing", deliveryCounts.getOrDefault("preparing",
        // 0L));
        // completeCounts.put("shipping", deliveryCounts.getOrDefault("shipping", 0L));
        // completeCounts.put("completed", deliveryCounts.getOrDefault("completed",
        // 0L));
        // completeCounts.put("cancelled", deliveryCounts.getOrDefault("cancelled",
        // 0L));

        // return ResponseEntity.ok(Map.of("deliveryCounts", completeCounts));
        // }

}
