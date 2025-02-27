package app.scit46.ufc.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import app.scit46.ufc.dto.MaterialDonationDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.campaign.CampaignGoalDTO;
import app.scit46.ufc.dto.campaign.CampaignTagDTO;
import app.scit46.ufc.dto.custom.RewardListDTO;
import app.scit46.ufc.service.MaterialDonationService;
import app.scit46.ufc.service.campaign.CampaignGoalService;
import app.scit46.ufc.service.campaign.CampaignService;
import app.scit46.ufc.service.cloudflare.ImageService;
import app.scit46.ufc.service.tag.CampaignTagService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/campaign")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final ImageService imageService;
    private final MaterialDonationService materialDonationService;
    private final CampaignGoalService campaignGoalService;
    private final CampaignTagService campaignTagService;

    @GetMapping("/all")
    public String allCampaign(Model model, @RequestParam(defaultValue = "") String searchKeyword) {
        List<CampaignDTO> campaigns = campaignService.readCampaignList(searchKeyword);
        model.addAttribute("campaigns", campaigns);
        return "campaign/all-campaign";
    }

    @GetMapping("/{id}")
    public String detailCampaign(@PathVariable Long id, Model model) {
        // 캠페인 조회 (없을 경우 예외 처리 또는 별도 로직 추가)
        CampaignDTO campaign = campaignService.readCampaign(id);

        List<CampaignTagDTO> tags = campaignTagService.findTagsByCampaignId(id);
        final String DEFAULT_IMAGE = "/static/images/fix/logo.png";

        // 캠페인 이미지 처리
        String imageUrl = (campaign.getPhoto() != null)
                ? imageService.getImageUrl(campaign.getPhoto().getImageId())
                : DEFAULT_IMAGE;
        model.addAttribute("imageUrl", imageUrl);

        // 크리에이터 이미지 처리
        String creatorImageUrl = (campaign.getCreatedBy() != null
                && campaign.getCreatedBy().getBusinessCert() != null)
                        ? imageService.getImageUrl(campaign.getCreatedBy().getBusinessCert().getImageId())
                        : DEFAULT_IMAGE;
        model.addAttribute("creatorimageUrl", creatorImageUrl);

        List<CampaignGoalDTO> campaignGoalDtos = campaignGoalService.getCampaignGoalsByCampaignId(id);
        List<MaterialDonationDTO> materialDonationDtos = materialDonationService.findDonationByCampaign(id);

        int totalDonors = materialDonationDtos.size();
        int totalQuantity = materialDonationDtos.stream()
                .mapToInt(MaterialDonationDTO::getQuantity)
                .sum();

        model.addAttribute("tags", tags);
        model.addAttribute("campaign", campaign);
        model.addAttribute("campaignGoalDTOS", campaignGoalDtos);
        model.addAttribute("totalDonors", totalDonors);
        model.addAttribute("totalQuantity", totalQuantity);
        //log.info(campaign.toString());

        return "campaign/detail-campaign";
    }

    @GetMapping("/expected")
    public String expectedCampaign() {
        return "campaign/expected-campaign";
    }

    @GetMapping("/pay")
    public String payCampaign(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false); // 세션 가져오기
        Long loginUserId = null; // 기본값 설정

        if (session != null) {
            loginUserId = (Long) session.getAttribute("loginUserId"); // 세션이 존재할 때만 값 가져오기
        }
        String username = request.getUserPrincipal().getName();
        model.addAttribute("username", username);
        return "campaign/pay-campaign";
    }

    @GetMapping("/intro")
    public String intro() {
        return "campaign/intro-campaign";
    }

    @GetMapping("/create") // 헤더에 존재하는 사용자 정보를 가져옴?
    public String create(HttpServletRequest request, Model model) {
        String username = request.getUserPrincipal().getName();
        //model.addAttribute("tags", tagService.readTagList()); // 추천태그 5개 추천 데이터
        model.addAttribute("username", username);
        return "campaign/create-campaign";
    }

    // 캠페인 수정 페이지
    @GetMapping("/update/{id}")
    public String update(@PathVariable Long id, Model model, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        model.addAttribute("username", username);
        CampaignDTO campaign = campaignService.readCampaign(id);
        //ImageID 대신 ImageUrl 전달
        campaign.getPhoto().setImageId(imageService.getImageUrl(campaign.getPhoto().getImageId()));
        model.addAttribute("campaign", campaign);
        List<String> campaignTags = campaignService.getCampaignTags(id);
        model.addAttribute("campaignTags", campaignTags);
        List<RewardListDTO> rewards = campaignService.convertCampaignFundingAndRewards(id);
        model.addAttribute("rewards", rewards);
        String rewardTitle = campaign.getTitle();
        return "campaign/update-campaign";
    }

    @GetMapping("/active")
    public String active() {
        return "campaign/active-campaign";
    }

    @GetMapping("/upcoming")
    public String upcoming() {
        return "campaign/upcoming-campaign";
    }

}
