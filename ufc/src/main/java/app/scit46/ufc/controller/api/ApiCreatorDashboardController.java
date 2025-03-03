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
        private final RewardDeliveryService rewardDeliveryService;

        /**
         * 기부 내역 조회
         * 
         * @param session
         * @return
         */
        @GetMapping("/donation/orders")
        public ResponseEntity<Map<String, Object>> getDonationOrders(HttpSession session) {
                Long creatorId = (Long) session.getAttribute("creatorId");
                if (creatorId == null) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Unauthorized"));
                }

                List<Long> campaignIds = campaignService.getCampaignIdsByCreator(creatorId);

                if (campaignIds.isEmpty()) {
                        return ResponseEntity.ok(Map.of(
                                        "campaigns", List.of(),
                                        "donations", List.of(),
                                        "donationCounts",
                                        Map.of("pending", 0, "processing", 0, "rejected", 0, "approved", 0)));
                }

                Map<String, Object> donationData = materialDonationService.getDonationData(campaignIds);

                return ResponseEntity.ok(donationData);
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

                materialDonationService.updateDonationStatus(donationId, isApproved);

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

                List<Long> campaignIds = campaignService.getCampaignIdsByCreator(creatorId);

                // ✅ 서비스 계층으로 로직 이동
                Map<String, Long> donationCounts = materialDonationService.getDonationCountsByCampaignIds(campaignIds);

                return ResponseEntity.ok(Map.of("donationCounts", donationCounts));
        }

        @GetMapping("/reward/deliveries")
        public ResponseEntity<Map<String, Object>> getRewardDeliveries(HttpSession session) {
                // 1️⃣ 세션에서 creatorId 가져오기
                Long creatorId = (Long) session.getAttribute("creatorId");
                if (creatorId == null) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Unauthorized"));
                }

                // 2️⃣ 해당 크리에이터의 성공한 캠페인 ID 목록 조회
                List<Long> campaignIds = campaignService.getSuccessfulCampaignIdsByCreator(creatorId);

                if (campaignIds.isEmpty()) {
                        return ResponseEntity.ok(Map.of(
                                        "campaigns", List.of(),
                                        "rewardDeliveries", List.of(),
                                        "deliveryCounts",
                                        Map.of("pending", 0, "shipping", 0, "completed", 0, "cancelled", 0)));
                }

                // 3️⃣ 성공한 캠페인의 리워드 배송 내역 조회
                Map<String, Object> rewardDeliveryData = rewardDeliveryService.getRewardDeliveryData(campaignIds);

                return ResponseEntity.ok(rewardDeliveryData);
        }

}
