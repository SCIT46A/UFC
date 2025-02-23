package app.scit46.ufc.controller;

import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.dto.MaterialDonationDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.service.UserService;
import app.scit46.ufc.service.campaign.CampaignService;
import app.scit46.ufc.service.MaterialDonationService;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/creator/dashboard")
@RequiredArgsConstructor
public class CreatorDashboardController {

    private final UserService userService;
    private final CampaignService campaignService;
    private final MaterialDonationService materialDonationService;
    private final Logger logger = LoggerFactory.getLogger(CreatorDashboardController.class);

    /**
     * 🔹 창작자 대시보드 메인 페이지
     * - 로그인한 창작자의 `creatorId`를 유지하고, 이후 모든 Fragment에서도 전달됨.
     */
    @GetMapping("")
    public String creatorDashboard(Model model, Authentication authentication, HttpSession session) {
        // 🔹 로그인한 유저 정보 가져오기
        String userIdentity = authentication.getName();
        UserEntity currentUser = userService.findUserByIdentity(userIdentity);

        if (currentUser == null || !currentUser.getRoles().contains("ROLE_CREATOR")) {
            return "redirect:/error/403";
        }

        // 🔹 CreatorEntity 조회 및 세션에 저장
        CreatorEntity creator = currentUser.getCreators().get(0);
        Long creatorId = creator.getCreatorId();
        session.setAttribute("creatorId", creatorId); // ✅ 세션에 저장

        // 🔹 로그 출력
        logger.info("📌 세션에 저장된 creatorId: {}", session.getAttribute("creatorId"));

        model.addAttribute("creatorId", creatorId);
        return "dashboard/creator-dashboard";
    }

    /**
     * 🔹 제품 등록 페이지
     */
    @GetMapping("/products/register")
    public String getProductRegisterPage(Model model, Authentication authentication, HttpSession session) {
        return addCreatorIdToModel(model, authentication, session, "dashboard/product-register :: product-register");
    }

    /**
     * 🔹 제품 관리 페이지
     */
    @GetMapping("/products/management")
    public String getProductManagementPage(Model model, Authentication authentication, HttpSession session) {
        return addCreatorIdToModel(model, authentication, session,
                "dashboard/product-management :: product-management");
    }

    /**
     * 🔹 주문 관리 페이지
     */
    @GetMapping("/products/orders")
    public String getOrderManagementPage(Model model, Authentication authentication, HttpSession session) {
        return addCreatorIdToModel(model, authentication, session, "dashboard/product-orders :: product-orders");
    }

    /**
     * 🔹 정산 관리 페이지
     */
    @GetMapping("/settlements")
    public String getSettlementManagementPage(Model model, Authentication authentication, HttpSession session) {
        return addCreatorIdToModel(model, authentication, session, "dashboard/settlements :: settlements");
    }

    /**
     * 🔹 캠페인 관리 페이지
     */
    @GetMapping("/campaigns/management")
    public String getCampaignManagementPage(HttpSession session, Model model) {
        // 🔹 세션에서 creatorId 가져오기
        Long creatorId = (Long) session.getAttribute("creatorId");

        // ✅ 로그 출력으로 확인
        if (creatorId == null) {
            logger.warn("❌ 세션에 creatorId가 없음");
            return "redirect:/error/403";
        }

        logger.info("📌 [Controller] 세션에서 가져온 creatorId: {}", creatorId);

        // 🔹 Creator ID로 캠페인 조회
        List<CampaignDTO> campaigns = campaignService.getCampaignsByCreator(creatorId);
        model.addAttribute("campaigns", campaigns);
        model.addAttribute("creatorId", creatorId);

        return "dashboard/campaign-management :: campaign-management";
    }

    /**
     * 🔹 기부 주문 관리 페이지
     */
    @GetMapping("/campaigns/donation/orders")
    public String getDonationOrdersPage(Model model, Authentication authentication, HttpSession session) {

        // 🔹 세션에서 creatorId 가져오기
        Long creatorId = (Long) session.getAttribute("creatorId");

        if (creatorId == null) {
            return "redirect:/error/403"; // ❌ 창작자가 아니면 접근 금지
        }

        // 🔹 해당 창작자의 캠페인 목록 가져오기
        List<CampaignDTO> campaigns = campaignService.getCampaignsByCreator(creatorId);
        model.addAttribute("campaigns", campaigns);

        // 🔹 캠페인 ID → 캠페인 제목 매핑
        Map<Long, String> campaignIdTitleMap = campaigns.stream()
                .collect(Collectors.toMap(CampaignDTO::getCampaignId, CampaignDTO::getTitle));
        model.addAttribute("campaignIdTitleMap", campaignIdTitleMap);

        // 🔹 해당 창작자의 캠페인 ID 목록 추출
        List<Long> campaignIds = campaigns.stream()
                .map(CampaignDTO::getCampaignId)
                .toList();

        // 🔹 해당 창작자의 캠페인 ID들에 해당하는 기부 내역 가져오기
        List<MaterialDonationDTO> donations = materialDonationService.getDonationsByCampaignIds(campaignIds);
        model.addAttribute("donations", donations);

        return "dashboard/donation-orders :: donation-orders";
    }

    /**
     * 🔹 리워드 배송 페이지
     */
    @GetMapping("/campaigns/reward/delivery")
    public String getRewardDeliveryPage(Model model, Authentication authentication, HttpSession session) {
        return addCreatorIdToModel(model, authentication, session, "dashboard/reward-delivery :: reward-delivery");
    }

    /**
     * 🔹 리뷰 관리 페이지
     */
    @GetMapping("/reviews")
    public String getReviewsPage(Model model, Authentication authentication, HttpSession session) {
        return addCreatorIdToModel(model, authentication, session, "dashboard/reviews :: reviews");
    }

    /**
     * 🔹 문의 관리 페이지
     */
    @GetMapping("/inquiries")
    public String getInquiriesPage(Model model, Authentication authentication, HttpSession session) {
        return addCreatorIdToModel(model, authentication, session, "dashboard/inquiries :: inquiries");
    }

    /**
     * ✅ **창작자 ID를 모델에 추가하는 공통 메서드**
     * - 모든 Fragment에서 `creatorId`를 유지하도록 도와줌.
     */
    private String addCreatorIdToModel(Model model, Authentication authentication, HttpSession session, String view) {
        Long creatorId = (Long) session.getAttribute("creatorId");

        if (creatorId == null) {
            String userIdentity = authentication.getName();
            UserEntity currentUser = userService.findUserByIdentity(userIdentity);

            if (currentUser == null || !currentUser.getRoles().contains("ROLE_CREATOR")) {
                return "redirect:/error/403";
            }

            CreatorEntity creator = currentUser.getCreators().get(0);
            creatorId = creator.getCreatorId();
            session.setAttribute("creatorId", creatorId); // ✅ 세션에 다시 저장
        }

        model.addAttribute("creatorId", creatorId);
        return view;
    }
    // /**
    // * 🔹 **세션 값 확인 API** (디버깅 용도)
    // * - 현재 세션에 `creatorId`가 있는지 확인하는 API
    // */
    // @GetMapping("/session/creatorId")
    // @ResponseBody
    // public ResponseEntity<String> getSessionCreatorId(HttpSession session) {
    // Long creatorId = (Long) session.getAttribute("creatorId");
    // if (creatorId == null) {
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ 세션에 creatorId
    // 없음");
    // }
    // return ResponseEntity.ok("📌 현재 세션에 저장된 creatorId: " + creatorId);
    // }
}
