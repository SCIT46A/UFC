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
    public String getCampaignManagementPage(Model model, Authentication authentication, HttpSession session) {
        return addCreatorIdToModel(model, authentication, session,
                "dashboard/campaign-management :: campaign-management");
    }

    /**
     * 🔹 기부 주문 관리 페이지
     */
    @GetMapping("/campaigns/donation/orders")
    public String getDonationOrdersPage(Model model, Authentication authentication, HttpSession session) {
        return addCreatorIdToModel(model, authentication, session, "dashboard/donation-orders :: donation-orders");
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
}
