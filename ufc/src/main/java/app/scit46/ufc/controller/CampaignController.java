package app.scit46.ufc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.scit46.ufc.dto.LikeDTO;
import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.exception.DBNotFoundException;
import app.scit46.ufc.service.LikeService;
import app.scit46.ufc.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.type.TypeReference; // ✅ 여기가 중요함!
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
@Slf4j
public class CampaignController {

    private final CampaignService campaignService;

    private final CampaignTagService campaignTagService;

    private final ImageService imageService;

    private final CampaignGoalService campaignGoalService;

    private final MaterialDonationService materialDonationService;

    private final LikeService likeService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @GetMapping("/all")
    public String allCampaign(Model model, @RequestParam(defaultValue = "") String searchKeyword) {
        List<CampaignDTO> campaigns = campaignService.readCampaignList(searchKeyword);
        model.addAttribute("campaigns", campaigns);
        return "campaign/all-campaign";
    }

    @GetMapping("/{id}")
    public String detailCampaign(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 세션 가져오기
        Long loginUserId = null; // 기본값 설정
        if (session != null) {
            loginUserId = (Long) session.getAttribute("loginUserId"); // 세션이 존재할 때만 값 가져오기
            model.addAttribute("loginUserId", loginUserId);
        }
        // 캠페인 조회 (없을 경우 예외 처리 또는 별도 로직 추가)
        CampaignDTO campaign = campaignService.readCampaign(id);
        // campaign_status가 false일때, userId랑 creator에서 받아온 userId랑 다르면 alert 띄우고 쫒아내기
        Long creatorId = campaign.getCreatedBy().getOwnUser().getUserId();
        Integer status = campaign.getCampaignStatus();
        if (status == 0) {
            if (loginUserId == null || !loginUserId.equals(creatorId)) {
                return "redirect:/";
            }
        }
        if (loginUserId == null) {

            model.addAttribute("status", 0);
        } else if (!loginUserId.equals(creatorId)) {
            model.addAttribute("status", 0);
        } else {
            model.addAttribute("status", 1);

        }

        List<CampaignTagDTO> tags = campaignTagService.findTagsByCampaignId(id);
        final String DEFAULT_IMAGE = "/static/images/fix/logo.png";

        // 캠페인 이미지 처리
        String imageUrl = (campaign.getPhoto() != null)
                ? imageService.getImageUrl(campaign.getPhoto().getImageId())
                : DEFAULT_IMAGE;
        model.addAttribute("imageUrl", imageUrl);

        // 크리에이터 이미지 처리
        String creatorImageUrl = (campaign.getCreatedBy() != null

                && campaign.getCreatedBy().getProImgUrl() != null)
                        ? imageService.getImageUrl(campaign.getCreatedBy().getProImgUrl().getImageId())
                        : DEFAULT_IMAGE;

        model.addAttribute("creatorimageUrl", creatorImageUrl);

        List<CampaignGoalDTO> campaignGoalDtos = campaignGoalService.findAll(id);
        List<MaterialDonationDTO> materialDonationDtos = materialDonationService.findDonationByCampaign(id);

        int totalDonors = materialDonationDtos.size();
        int totalQuantity = materialDonationDtos.stream()
                .mapToInt(MaterialDonationDTO::getQuantity)
                .sum();

        boolean campaignLike = likeService.likeCheck(campaign.getCampaignId(), loginUserId, "campaign");
        boolean creatorLike = likeService.likeCheck(campaign.getCreatedBy().getCreatorId(), loginUserId, "creator");
        model.addAttribute("campaignLike", campaignLike);
        model.addAttribute("creatorLike", creatorLike);

        model.addAttribute("tags", tags);
        model.addAttribute("campaign", campaign);
        model.addAttribute("campaignGoalDTOS", campaignGoalDtos);
        model.addAttribute("totalDonors", totalDonors);
        model.addAttribute("totalQuantity", totalQuantity);

        return "campaign/detail-campaign";
    }

    @GetMapping("/test/{id}")
    public String detailCampaign(@PathVariable("id") Long id) {
        // // 캠페인 조회 (없을 경우 예외 처리 또는 별도 로직 추가)
        CampaignDTO campaign = campaignService.readCampaign(id);

        return "campaign/detail-campaign-test";
    }

    @GetMapping("/expected")
    public String expectedCampaign() {
        return "campaign/expected-campaign";
    }

    @GetMapping("/pay")
    public String payCampaign(@RequestParam(name = "donationDetails", required = false) String donationDetails,
            HttpServletRequest request, Model model) {
        final String DEFAULT_IMAGE = "/static/images/fix/logo.png";

        HttpSession session = request.getSession(false);
        Long loginUserId = null;
        if (session != null) {
            Object loginUserObj = session.getAttribute("loginUserId");
            if (loginUserObj != null) {
                if (loginUserObj instanceof Long) {
                    loginUserId = (Long) loginUserObj;
                } else if (loginUserObj instanceof String) {
                    loginUserId = Long.parseLong((String) loginUserObj);
                }
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON 문자열을 List<Map<String, Object>> 형태로 변환
            List<Map<String, Object>> donationList = objectMapper.readValue(
                    donationDetails, new TypeReference<List<Map<String, Object>>>() {
                    });

            // campaignId 처리 (기존 코드)
            if (!donationList.isEmpty() && donationList.get(0).containsKey("campaignId")) {
                Object campaignIdObj = donationList.get(0).get("campaignId");
                Long campaignId = null;
                if (campaignIdObj instanceof Number) {
                    campaignId = ((Number) campaignIdObj).longValue();
                } else if (campaignIdObj instanceof String && !((String) campaignIdObj).isEmpty()) {
                    campaignId = Long.parseLong((String) campaignIdObj);
                }
                List<CampaignGoalDTO> campaignGoalDtos = campaignGoalService.findAll(campaignId);
                model.addAttribute("campaignGoalDTOS", campaignGoalDtos);

                if (campaignId != null) {
                    CampaignDTO campaign = campaignService.readCampaign(campaignId);
                    model.addAttribute("campaign", campaign);
                    String imageUrl = (campaign.getPhoto() != null)
                            ? imageService.getImageUrl(campaign.getPhoto().getImageId())
                            : DEFAULT_IMAGE;
                    model.addAttribute("imageUrl", imageUrl);
                } else {
                    System.out.println("campaignId가 null입니다.");
                }
            } else {
                System.out.println("donationDetails에 campaignId가 없습니다.");
            }

            // name으로 그룹화하여 total 합산 처리
            Map<String, Map<String, Object>> donationSummary = new HashMap<>();
            for (Map<String, Object> donation : donationList) {
                String name = donation.get("name").toString();
                int total = 0;
                Object totalObj = donation.get("total");
                if (totalObj instanceof Number) {
                    total = ((Number) totalObj).intValue();
                } else if (totalObj instanceof String) {
                    total = Integer.parseInt((String) totalObj);
                }

                // materialId 추출
                Object materialIdObj = donation.get("materialId");
                int materialId = 0;
                if (materialIdObj instanceof Number) {
                    materialId = ((Number) materialIdObj).intValue();
                } else if (materialIdObj instanceof String && !((String) materialIdObj).isEmpty()) {
                    materialId = Integer.parseInt((String) materialIdObj);
                }

                // rewardId 추출 (존재하면 저장, 없으면 null)
                Object rewardIdObj = donation.get("rewardId");
                Object rewardId = (rewardIdObj != null
                        && !(rewardIdObj instanceof String && ((String) rewardIdObj).trim().isEmpty()))
                                ? rewardIdObj
                                : null;

                if (donationSummary.containsKey(name)) {
                    Map<String, Object> summary = donationSummary.get(name);
                    summary.put("total", (int) summary.get("total") + total);
                    // 기존에 저장된 rewardId가 List 형태인지 확인
                    List<Object> rewardIds = (List<Object>) summary.get("rewardId");
                    if (rewardIds == null) {
                        rewardIds = new ArrayList<>();
                    }
                    // 새 donation에서 rewardId가 null이 아니라면 리스트에 추가 (중복 방지)
                    if (rewardId != null && !rewardIds.contains(rewardId)) {
                        rewardIds.add(rewardId);
                    }
                    summary.put("rewardId", rewardIds);
                } else {
                    Map<String, Object> summary = new HashMap<>();
                    summary.put("total", total);
                    summary.put("materialId", materialId);
                    List<Object> rewardIds = new ArrayList<>();
                    if (rewardId != null) {
                        rewardIds.add(rewardId);
                    }
                    summary.put("rewardId", rewardIds);
                    donationSummary.put(name, summary);
                }
            }
            model.addAttribute("donationSummary", donationSummary);

            // donationList에서 rewardId가 존재하는 항목만 별도로 담기 (rewardId와 count만)
            List<Map<String, Object>> donationRewardList = new ArrayList<>();
            for (Map<String, Object> donation : donationList) {
                Object rewardIdObj = donation.get("rewardId");
                // rewardId가 null이 아니고 빈 문자열이 아닌 경우에만 처리
                if (rewardIdObj != null
                        && !(rewardIdObj instanceof String && ((String) rewardIdObj).trim().isEmpty())) {
                    Map<String, Object> rewardItem = new HashMap<>();
                    rewardItem.put("rewardId", rewardIdObj);

                    // count 값 추출
                    int count = 0;
                    Object countObj = donation.get("count");
                    if (countObj instanceof Number) {
                        count = ((Number) countObj).intValue();
                    } else if (countObj instanceof String && !((String) countObj).isEmpty()) {
                        count = Integer.parseInt((String) countObj);
                    }
                    rewardItem.put("count", count);

                    donationRewardList.add(rewardItem);
                }
            }
            // donationRewardList 생성 후
            ObjectMapper mapper = new ObjectMapper();
            String donationRewardListJson = mapper.writeValueAsString(donationRewardList);
            model.addAttribute("donationRewardListJson", donationRewardListJson);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("JSON 파싱 오류: " + e.getMessage());
        }

        if (loginUserId != null) {
            try {
                UserDTO user = userService.readUserById(loginUserId);
                model.addAttribute("user", user);
            } catch (DBNotFoundException e) {
                model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
            }
        }

        return "campaign/pay-campaign";
    }

    @GetMapping("/intro")
    public String intro() {
        return "campaign/intro-campaign";
    }

    @GetMapping("/create") // 헤더에 존재하는 사용자 정보를 가져옴?
    public String create(HttpServletRequest request, Model model,
            @RequestParam(value = "iframe", required = false, defaultValue = "false") boolean iframe) {
        String username = request.getUserPrincipal().getName();
        // model.addAttribute("tags", tagService.readTagList()); // 추천태그 5개 추천 데이터
        model.addAttribute("username", username);
        model.addAttribute("iframe", iframe);
        return "campaign/create-campaign";
    }

    // 캠페인 수정 페이지
    @GetMapping("/update/{id}")
    public String update(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();

        CampaignDTO campaign = campaignService.readCampaign(id);
        // ImageID 대신 ImageUrl 전달
        campaign.getPhoto().setImageId(imageService.getImageUrl(campaign.getPhoto().getImageId()));

        List<String> campaignTags = campaignService.getCampaignTags(id);

        List<RewardListDTO> rewards = campaignService.convertCampaignFundingAndRewards(id);

        model.addAttribute("username", username);
        model.addAttribute("campaign", campaign);
        model.addAttribute("campaignTags", campaignTags);
        model.addAttribute("rewards", rewards);

        String rewardTitle = campaign.getTitle();

        log.info("rewardTmp: {}", rewards);

        if (rewards != null && !rewards.isEmpty()) {

            log.info("reward list size: {}", rewards.size());
            log.info("reward list contents: {}", rewards);
        }

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

    // 가이드 새롭게 추가
    @GetMapping("/upcycling_campaign_guide")
    public String guide() {
        return "upcycling_campaign_guide";
    }

}
