package app.scit46.ufc.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import app.scit46.ufc.service.campaign.CampaignService;
import app.scit46.ufc.service.MaterialDonationService;
import app.scit46.ufc.service.reward.RewardDeliveryService;
import app.scit46.ufc.service.product.ProductService;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import app.scit46.ufc.dto.reward.InvoiceUpdateRequest;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/creator/dashboard")
@RequiredArgsConstructor
public class ApiCreatorDashboardController {

        private final CampaignService campaignService;
        private final MaterialDonationService materialDonationService;
        private final RewardDeliveryService rewardDeliveryService;
        private final ProductService productService;

        /**
         * 
         * @param session
         * @return
         */
        @GetMapping("/campaigns/management")
        public ResponseEntity<List<Map<String, Object>>> getCampaigns(HttpSession session) {
                Long creatorId = (Long) session.getAttribute("creatorId");

                if (creatorId == null) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
                }

                List<Map<String, Object>> campaigns = campaignService.getCampaignsByCreator(creatorId);
                return ResponseEntity.ok(campaigns);
        }

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

        @PostMapping("/reward/deliveries/{rdeliveryId}")
        public ResponseEntity<?> updateInvoice(
                        @PathVariable("rdeliveryId") Long rdeliveryId,
                        @RequestBody Map<String, String> requestBody) {

                try {
                        // 🚀 1. 요청에서 courier(택배사)와 trackingNumber(송장번호) 가져오기
                        String courier = requestBody.get("courier");
                        String trackingNumber = requestBody.get("trackingNumber");

                        // 🚨 필수 값 검증 (비어 있는 경우 오류 응답)
                        if (courier == null || courier.isEmpty() || trackingNumber == null
                                        || trackingNumber.isEmpty()) {
                                return ResponseEntity.badRequest().body("🚨 택배사와 송장번호를 입력해야 합니다.");
                        }

                        // 🚀 2. 송장번호 업데이트 (DB 저장)
                        rewardDeliveryService.updateInvoice(rdeliveryId, courier, trackingNumber);

                        return ResponseEntity.ok("✅ 송장 정보 업데이트 성공");

                } catch (IllegalArgumentException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("🚨 " + e.getMessage());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("❌ 서버 오류: " + e.getMessage());
                }
        }

        @PostMapping("/reward/deliveries/batch-update")
        public ResponseEntity<?> updateInvoices(@RequestBody List<InvoiceUpdateRequest> updateRequests) {
                rewardDeliveryService.updateInvoices(updateRequests);
                return ResponseEntity.ok(Collections.singletonMap("message", "송장번호 업데이트 완료"));
        }

        /**
         * 크리에이터 상품 관리
         * 
         * @param session
         * @return
         */
        @GetMapping("/products")
        public ResponseEntity<?> getCreatorProducts(HttpSession session) {
                try {
                        Long creatorId = (Long) session.getAttribute("creatorId");
                        if (creatorId == null) {
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(Collections.singletonMap("error", "로그인이 필요합니다."));
                        }

                        List<Map<String, Object>> products = productService.getProductsByCreator(creatorId);

                        return ResponseEntity.ok(products);
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Collections.singletonMap("error",
                                                        "상품 데이터를 불러오는 중 오류 발생: " + e.getMessage()));
                }
        }

}
