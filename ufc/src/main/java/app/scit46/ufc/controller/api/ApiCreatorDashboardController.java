package app.scit46.ufc.controller.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.scit46.ufc.dto.delivery.InvoiceUpdateRequest;
import app.scit46.ufc.service.CourierService;
import app.scit46.ufc.service.CreatorService;
import app.scit46.ufc.service.MaterialDonationService;
import app.scit46.ufc.service.campaign.CampaignService;
import app.scit46.ufc.service.product.PayService;
import app.scit46.ufc.service.product.ProductDeliveryService;
import app.scit46.ufc.service.product.ProductPaymentService;
import app.scit46.ufc.service.product.ProductService;
import app.scit46.ufc.service.reward.RewardDeliveryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/creator/dashboard")
@RequiredArgsConstructor
public class ApiCreatorDashboardController {

        private final CampaignService campaignService;
        private final MaterialDonationService materialDonationService;
        private final RewardDeliveryService rewardDeliveryService;
        private final ProductService productService;
        private final ProductPaymentService productPaymentService;
        private final ProductDeliveryService productDeliveryService;
        private final CourierService courierService;
        private final PayService payService;
        private final CreatorService creatorService;

        /**
         * 
         * @param session
         * @return
         */

        @GetMapping("/main")
        public ResponseEntity<String> getCreatorDashboardData(@RequestParam("creatorId") Long creatorId) {
                String dashboardData = creatorService.getDashboardData(creatorId);
                return ResponseEntity.ok(dashboardData);
        }

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
                Long creatorId = (Long) session.getAttribute("creatorId");
                if (creatorId == null) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Unauthorized"));
                }

                List<Long> campaignIds = campaignService.getSuccessfulCampaignIdsByCreator(creatorId);

                if (campaignIds.isEmpty()) {
                        return ResponseEntity.ok(Map.of(
                                        "campaigns", List.of(),
                                        "rewardDeliveries", List.of(),
                                        "deliveryCounts",
                                        Map.of("pending", 0, "shipping", 0, "completed", 0, "cancelled", 0)));
                }

                Map<String, Object> rewardDeliveryData = rewardDeliveryService.getRewardDeliveryData(campaignIds);

                return ResponseEntity.ok(rewardDeliveryData);
        }

        @PostMapping("/reward/deliveries/{rdeliveryId}")
        public ResponseEntity<?> updateRewardInvoice(
                        @PathVariable("rdeliveryId") Long rdeliveryId,
                        @RequestBody Map<String, String> requestBody) {

                try {
                        String courier = requestBody.get("courier");
                        String trackingNumber = requestBody.get("trackingNumber");

                        if (courier == null || courier.isEmpty() || trackingNumber == null
                                        || trackingNumber.isEmpty()) {
                                return ResponseEntity.badRequest().body("🚨 택배사와 송장번호를 입력해야 합니다.");
                        }

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
        public ResponseEntity<?> updateRewardInvoices(@RequestBody List<InvoiceUpdateRequest> updateRequests) {
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

        @PatchMapping("/products/{productId}")
        public ResponseEntity<?> updateProduct(
                        @PathVariable("productId") Long productId,
                        @RequestBody Map<String, Object> requestBody) {
                try {
                        productService.updateProduct(productId, requestBody);
                        return ResponseEntity.ok(Collections.singletonMap("message", "상품 업데이트 완료"));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Collections.singletonMap("error", "상품 업데이트 중 오류 발생: " + e.getMessage()));
                }
        }

        @PatchMapping("/products/{productId}/tags")
        public ResponseEntity<?> updateProductTags(
                        @PathVariable("productId") Long productId,
                        @RequestBody Map<String, List<String>> requestBody) {
                try {
                        List<String> newTags = requestBody.get("tags"); // ✅ 사용자 입력 태그
                        productService.updateProductTags(productId, newTags); // ✅ 서비스 호출
                        return ResponseEntity.ok(Collections.singletonMap("message", "태그 업데이트 완료"));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Collections.singletonMap("error", "태그 업데이트 중 오류 발생: " + e.getMessage()));
                }
        }

        @PatchMapping("/products/{productId}/status")
        public ResponseEntity<?> updateProductStatus(@PathVariable("productId") Long productId,
                        @RequestBody Map<String, Integer> requestBody) {
                productService.updateProductStatus(productId, requestBody.get("status"));
                return ResponseEntity.ok(Collections.singletonMap("message", "상태 업데이트 완료"));
        }

        @PatchMapping("/products/{productId}/delete")
        public ResponseEntity<?> softDeleteProduct(@PathVariable("productId") Long productId) {
                try {
                        System.out.println("🗑️ 상품 삭제 요청: " + productId);
                        productService.deleteProduct(productId);
                        return ResponseEntity.ok(Collections.singletonMap("message", "상품이 삭제(비활성화)되었습니다."));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Collections.singletonMap("error", "상품 삭제 실패: " + e.getMessage()));
                }
        }

        @GetMapping("/products/orders")
        public ResponseEntity<Map<String, Object>> getOrders(HttpSession session) {
                Long creatorId = (Long) session.getAttribute("creatorId");
                if (creatorId == null) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Unauthorized"));
                }

                Map<String, Object> orders = productPaymentService.getOrdersByCreator(creatorId);
                return ResponseEntity.ok(orders);
        }

        @PostMapping("/products/orders/process/{payId}")
        public ResponseEntity<?> processOrder(@PathVariable("payId") Long payId) {
                System.out.println("🚀 발주 처리 요청: " + payId);
                try {
                        productPaymentService.processOrder(payId);
                        return ResponseEntity.ok(Map.of("message", "✅ 발주 처리 완료"));
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("🚨 " + e.getMessage());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("❌ 서버 오류: " + e.getMessage());
                }
        }

        @PostMapping("/products/orders/invoice/batch-update")
        public ResponseEntity<?> batchUpdateInvoices(@RequestBody List<InvoiceUpdateRequest> invoices) {
                try {
                        // ✅ 택배사 이름 → 코드 변환
                        for (InvoiceUpdateRequest invoice : invoices) {
                                invoice.setCourier(courierService.getCourierIdByName(invoice.getCourier()));
                        }

                        productDeliveryService.updateInvoices(invoices);
                        return ResponseEntity.ok(Collections.singletonMap("message", "✅ 송장번호 일괄 업데이트 성공"));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Collections.singletonMap("error", "❌ 송장번호 업데이트 실패: " + e.getMessage()));
                }
        }

        @PostMapping("/products/orders/invoice/{payId}")
        public ResponseEntity<?> updateProductInvoice(
                        @PathVariable("payId") Long payId,
                        @RequestBody Map<String, Object> requestBody) { // ✅ Object로 변경

                try {
                        String courier = (String) requestBody.get("courier");
                        String trackingNumber = (String) requestBody.get("trackingNumber");

                        // ✅ productId를 Long 타입으로 변환 (예외 방지)
                        Long productId;
                        try {
                                productId = Long.parseLong(requestBody.get("productId").toString());
                        } catch (Exception e) {
                                return ResponseEntity.badRequest()
                                                .body(Collections.singletonMap("error", "🚨 올바른 상품 ID가 아닙니다."));
                        }

                        if (courier == null || courier.isEmpty() || trackingNumber == null
                                        || trackingNumber.isEmpty()) {
                                return ResponseEntity.badRequest()
                                                .body(Collections.singletonMap("error", "🚨 택배사와 송장번호를 입력해야 합니다."));
                        }

                        productDeliveryService.updateInvoice(payId, productId, courier, trackingNumber);

                        return ResponseEntity.ok("✅ 송장 정보 업데이트 성공");

                } catch (IllegalArgumentException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("🚨 " + e.getMessage());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("❌ 서버 오류: " + e.getMessage());
                }
        }

        @PostMapping("/products/orders/cancel/{payId}")
        public ResponseEntity<?> approveCancelOrder(@PathVariable("payId") Long payId) {
                try {
                        payService.cancel(payId);
                        productPaymentService.approveCancel(payId);
                        return ResponseEntity.ok("✅ 주문 취소 승인 완료");
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("🚨 주문을 찾을 수 없음: " + e.getMessage());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("❌ 서버 오류: " + e.getMessage());
                }
        }

        /**
         * 정산 목록 조회 API
         */
        @GetMapping("/settlements")
        public ResponseEntity<List<Map<String, Object>>> getSettlements(HttpSession session) {
                Long creatorId = (Long) session.getAttribute("creatorId");

                if (creatorId == null) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
                }
                List<Map<String, Object>> settlements = productPaymentService.getSettlementList(creatorId);
                return ResponseEntity.ok(settlements);
        }

        /**
         * ✅ 정산 상세 정보 조회 API
         */
        @GetMapping("/settlements/details/{payId}")
        public ResponseEntity<Map<String, Object>> getSettlementDetails(@PathVariable("payId") Long payId) {
                System.out.println("📢 [SettlementController] 정산 상세 요청 - ID: " + payId);

                Map<String, Object> settlement = productPaymentService.getSettlementDetails(payId);

                if (settlement == null || settlement.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "정산 내역을 찾을 수 없습니다."));
                }

                return ResponseEntity.ok(settlement);
        }

        // /**
        // * 정산 완료 처리 API
        // */
        // @PostMapping("/settlements/complete")
        // public ResponseEntity<String> completeSettlement(@RequestBody List<Long>
        // payIds) {
        // productPaymentService.processSettlement(payIds);
        // return ResponseEntity.ok("정산 완료 처리되었습니다.");
        // }

}
