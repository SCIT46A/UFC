package app.scit46.ufc.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.service.campaign.CampaignService;
import app.scit46.ufc.service.MaterialDonationService;
import app.scit46.ufc.dto.MaterialDonationDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/creator/dashboard")
@RequiredArgsConstructor
public class ApiCreatorDashboardController {

        private final CampaignService campaignService;
        private final MaterialDonationService materialDonationService;

        /**
         * 🔹 기부 주문 데이터 제공 (프론트에서 AJAX 요청)
         */
        @GetMapping("/{creatorId}/donation-orders")
        public ResponseEntity<Map<String, Object>> getDonationOrders(@PathVariable("creatorId") Long creatorId) {
                // 🔹 해당 창작자의 캠페인 목록 가져오기
                List<CampaignDTO> campaigns = campaignService.getCampaignsByCreator(creatorId);
                System.out.println("🔹 creatorId: " + creatorId);

                // 🔹 캠페인 ID → 캠페인 제목 매핑
                Map<Long, String> campaignIdTitleMap = campaigns.stream()
                                .collect(Collectors.toMap(CampaignDTO::getCampaignId, CampaignDTO::getTitle));

                // 🔹 해당 창작자의 캠페인 ID 목록 추출
                List<Long> campaignIds = campaigns.stream()
                                .map(CampaignDTO::getCampaignId)
                                .toList();

                // 🔹 기부 내역 조회 (최대 100개)
                List<MaterialDonationDTO> donations = materialDonationService.getDonationsByCampaignIds(campaignIds,
                                100);

                // 🔹 기부 상태별 개수 계산
                Map<String, Long> donationCounts = donations.stream()
                                .collect(Collectors.groupingBy(MaterialDonationDTO::getStatus, Collectors.counting()));

                return ResponseEntity.ok(Map.of(
                                "campaigns", campaigns,
                                "campaignIdTitleMap", campaignIdTitleMap,
                                "donations", donations,
                                "donationCounts", donationCounts));
        }
}
