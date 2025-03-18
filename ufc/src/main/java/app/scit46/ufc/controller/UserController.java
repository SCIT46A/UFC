package app.scit46.ufc.controller;


import java.util.HashMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



import app.scit46.ufc.service.chat.ChatRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import app.scit46.ufc.dto.BadgeDTO;
import app.scit46.ufc.dto.CreatorDTO;
import app.scit46.ufc.dto.LikeDTO;
import app.scit46.ufc.dto.MaterialDonationDTO;
import app.scit46.ufc.dto.UserBadgeDTO;
import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.campaign.CampaignGoalDTO;
import app.scit46.ufc.dto.reward.RewardDeliveryDTO;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.entity.campaign.CampaignReviewEntity;
import app.scit46.ufc.exception.DBNotFoundException;
import app.scit46.ufc.service.BadgeService;
import app.scit46.ufc.service.CreatorService;
import app.scit46.ufc.service.LikeService;
import app.scit46.ufc.service.MaterialDonationService;
import app.scit46.ufc.service.UserBadgeService;
import app.scit46.ufc.service.UserService;
import app.scit46.ufc.service.campaign.CampaignGoalService;
import app.scit46.ufc.service.campaign.CampaignReviewService;
import app.scit46.ufc.service.campaign.CampaignService;
import app.scit46.ufc.service.cloudflare.ImageService;
import app.scit46.ufc.service.delivery.DeliveryService;
import app.scit46.ufc.service.reward.RewardDeliveryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;
    private final CampaignService campaignService;
    private final CampaignReviewService campaignReviewService;
    private final LikeService likeService;
    private final ImageService imageService;
    private final MaterialDonationService materialDonationService;

    private final BadgeService badgeService;
    private final UserBadgeService userBadgeService;
    private final CampaignGoalService campaignGoalService;
    private final RewardDeliveryService rewardDeliveryService;
    private final DeliveryService deliveryService;
    private final CreatorService creatorService;
    private final ChatRoomService chatRoomService;

    // 유저 기본페이지 조회
    @GetMapping({ "/", "" })
    public String index(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false); // 세션 가져오기
        Long userId = null; // 기본값 설정

        if (session != null) {
            userId = (Long) session.getAttribute("loginUserId"); // 세션이 존재할 때만 값 가져오기
            if (userId != null) {
                try {
                    // 사용자 정보를 데이터베이스에서 조회
                    UserDTO user = userService.readUserById(userId);
                    List<String> imageUrls = new ArrayList<>();
                    if (user.getPhoto() == null || user.getPhoto().getImageId() == null) {
                        imageUrls.add("/images/user/default_avatar.png");
                    } else {
                        imageUrls.add(imageService.getImageUrl(user.getPhoto().getImageId()));
                    }
                    List<LikeDTO> likes = likeService.getLikeByUserUserId(userId);
                    // ✅ `likes` 리스트를 분류 (creator_id 존재 여부 기준)
                    List<LikeDTO> creatorLikes = likes.stream()
                            .filter(like -> like.getCreator() != null)
                            .collect(Collectors.toList());

                    List<MaterialDonationDTO> materialDonations = materialDonationService
                                .getMaterialDonationsByUserId(user.getUserId()); 

                    model.addAttribute("creatorLikes", creatorLikes.size());
                    model.addAttribute("donationCount", materialDonations.size());
                    

                    model.addAttribute("user", user);
                    model.addAttribute("imageUrls", imageUrls);
                } catch (DBNotFoundException e) {
                    // 사용자 정보를 찾을 수 없는 경우 처리
                    model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
                }
            }
        }
        return "user/mypage-profile";
    }

    // 유저 도네이션 조회
    // @GetMapping("/donation")
    // public String donation(Model model) {
    // LocalDateTime donatedDate = LocalDateTime.now();
    // model.addAttribute("donatedDate", donatedDate);
    // return "user/mypage-donation";
    // }


        @GetMapping("/review")
        public String review (HttpServletRequest request,
                Model model,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "5") int size){
            HttpSession session = request.getSession(false);
            if (session != null) {
                Long userId = (Long) session.getAttribute("loginUserId");
                if (userId != null) {
                    try {
                        UserDTO user = userService.readUserById(userId);
                        // 페이징 처리된 리뷰 데이터를 가져옴
                        Page<CampaignReviewEntity> paging = campaignReviewService.getListByUser(userId, page, size);
                        List<CampaignReviewEntity> reviewList = paging.getContent();

                        // 리뷰 목록에서 캠페인 및 이미지 URL 정보를 추출
                        List<CampaignDTO> campaigns = new ArrayList<>();
                        List<String> imageUrls = new ArrayList<>();
                        
                        String userImageId = null;
                        
                         if (user.getPhoto() == null) {
                        userImageId = "/images/user/default_avatar.png";
                        }else{
                        userImageId = "https://imagedelivery.net/sXWs4txHKON-dqRmy35ZtA/"
                                + user.getPhoto().getImageId() + "/public";
                        }

                        for (CampaignReviewEntity reviewEntity : reviewList) {
                            // 변환하는 로직은 상황에 맞게 조정
                            CampaignDTO campaign = campaignService
                                    .getCampaignById(reviewEntity.getCampaignedBy().getCampaignId());
                            campaigns.add(campaign);
                            imageUrls.add(
                                    imageService.getImageUrl(reviewEntity.getCampaignedBy().getPhoto().getImageId()));
                        }
                        List<LikeDTO> likes = likeService.getLikeByUserUserId(userId);
                        // ✅ `likes` 리스트를 분류 (creator_id 존재 여부 기준)
                        List<LikeDTO> creatorLikes = likes.stream()
                                .filter(like -> like.getCreator() != null)
                                .collect(Collectors.toList());

                        List<MaterialDonationDTO> materialDonations = materialDonationService
                                .getMaterialDonationsByUserId(userId);

                        model.addAttribute("creatorLikes", creatorLikes.size());
                        model.addAttribute("donationCount", materialDonations.size());
                        model.addAttribute("imageUrls", imageUrls);
                        model.addAttribute("userImageId", userImageId);
                        // Page 객체를 그대로 전달해서 페이징 네비게이션에 활용
                        model.addAttribute("paging", paging);
                        model.addAttribute("user", user);
                        model.addAttribute("reviewCount", paging.getTotalElements());

                } catch (DBNotFoundException e) {
                    model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
                }
            }
        }
        return "user/mypage-review";
    }

    // List<CampaignEntity> campaign = donations.stream()
    // .map(donation ->
    // campaignService.campaignFindByCampaignId(donation.getCampaign().getCampaignId()))
    // .flatMap(List::stream)
    // .collect(Collectors.toList());

    // 유저 정보 수정 창 조회
    @GetMapping("/edit")
    public String edit(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false); // 세션 가져오기
        Long userId = null; // 기본값 설정
        if (session != null) {
            userId = (Long) session.getAttribute("loginUserId"); // 세션이 존재할 때만 값 가져오기
            if (userId != null) {
                try {
                    // 사용자 정보를 데이터베이스에서 조회
                    UserDTO user = userService.readUserById(userId);
                    List<String> imageUrls = new ArrayList<>();
                    imageUrls.add(imageService.getImageUrl(user.getPhoto().getImageId()));
                    model.addAttribute("user", user);
                    model.addAttribute("imageUrls", imageUrls);
                } catch (DBNotFoundException e) {
                    // 사용자 정보를 찾을 수 없는 경우 처리
                    model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
                }
            }
        }
        return "user/mypage-profile-edit";
    }






    @GetMapping("/donation")
    public String donation(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long userId = (Long) session.getAttribute("loginUserId");
            if (userId != null) {
                try {
                    UserDTO user = userService.readUserById(userId);
                    List<MaterialDonationDTO> materialDonations = materialDonationService.getMaterialDonationsByUserId(user.getUserId());
                    List<CampaignDTO> campaigns = new ArrayList<>();
                    List<String> imageUrls = new ArrayList<>();
                    List<Double> isAchivedList = new ArrayList<>();
                    List<String> rewardNameList = new ArrayList<>();

                    for (MaterialDonationDTO materialDonation : materialDonations) {
                        CampaignDTO campaign = materialDonation.getCampaign();
                        if (campaign != null && campaign.getPhoto() != null) {
                            campaigns.add(campaign);
                            imageUrls.add(imageService.getImageUrl(campaign.getPhoto().getImageId()));

                            int goal = campaignGoalService.getCampaignGoalByCampaignId(campaign.getCampaignId()).get(0)
                                    .getQuantityRequired();
                            int donationsum = materialDonationService.getMaterialDonationsByCampaignId(campaign.getCampaignId())
                                    .stream().mapToInt(MaterialDonationDTO::getQuantity).sum();

                            double achievement = (double) donationsum / goal * 100;
                            isAchivedList.add(achievement);

                            String rewardName = rewardDeliveryService.getRewardNameByDonationId(materialDonation.getDonationId());
                            rewardNameList.add(rewardName);
                        }
                    }

                    List<LikeDTO> likes = likeService.getLikeByUserUserId(userId);
                    List<LikeDTO> creatorLikes = likes.stream()
                            .filter(like -> like.getCreator() != null)
                            .collect(Collectors.toList());

                    Map<String, String> statusMap = new HashMap<>();
                    statusMap.put("approved", "확인되었어요 🙂");
                    statusMap.put("rejected", "거절되었어요 🙁");
                    statusMap.put("pending", "검토중이에요!");

                    String userImageId = (user.getPhoto() == null || user.getPhoto().getImageId() == null)
                            ? "/images/user/default_avatar.png"
                            : "https://imagedelivery.net/sXWs4txHKON-dqRmy35ZtA/" + user.getPhoto().getImageId() + "/public";

                    model.addAttribute("statusMap", statusMap);
                    model.addAttribute("isAchivedList", isAchivedList);
                    model.addAttribute("rewardNameList", rewardNameList);
                    model.addAttribute("creatorLikes", creatorLikes.size());
                    model.addAttribute("donationCount", materialDonations.size());
                    model.addAttribute("imageUrls", imageUrls);
                    model.addAttribute("campaigns", campaigns);
                    model.addAttribute("materialDonations", materialDonations);
                    model.addAttribute("user", user);
                    model.addAttribute("userImageId", userImageId);
                } catch (DBNotFoundException e) {
                    model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
                }
            }
        }
        return "user/mypage-donation";
    }





    // 회원탈퇴(status 1로 변경)
        @GetMapping("/delete")
        public String delete (
                HttpSession session,
                @RequestParam(name = "userId") Long userId){
            userService.delete(userId);
            session.removeAttribute("loginUserId");
            return "forward:/logout";
        }


        @PostMapping("/userUpdate")
        public String postMethodName (
                HttpServletRequest request, @ModelAttribute UserDTO userDTO, RedirectAttributes rttr){
            HttpSession session = request.getSession(false);
            userDTO.setUserId((Long) session.getAttribute("loginUserId"));
            userService.userUpdate(userDTO);

            return "redirect:/user";
        }



        @GetMapping("/badge")
        public String badge(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        Long userId = (Long) session.getAttribute("loginUserId");
        if (userId != null) {
        try {
            UserDTO user = userService.readUserById(userId);
            List<BadgeDTO> badges = badgeService.getBadges();
            List<UserBadgeDTO> userBadges = userBadgeService.getUserBadge(userId);

            // userBadges에서 보유한 뱃지들의 badgeId만 추출
            List<Long> userBadgeIds = userBadges.stream()
                    .map(ub -> ub.getBadge().getBadgeId())
                    .collect(Collectors.toList());

            String userImageId = "https://imagedelivery.net/sXWs4txHKON-dqRmy35ZtA/"
                    + user.getPhoto().getImageId() + "/public";
            
                    List<LikeDTO> likes = likeService.getLikeByUserUserId(userId);
            // ✅ `likes` 리스트를 분류 (creator_id 존재 여부 기준)
            List<LikeDTO> creatorLikes = likes.stream()
                    .filter(like -> like.getCreator() != null)
                    .collect(Collectors.toList());

            List<MaterialDonationDTO> materialDonations = materialDonationService
                    .getMaterialDonationsByUserId(userId);

            model.addAttribute("creatorLikes", creatorLikes.size());
            model.addAttribute("donationCount", materialDonations.size());
            model.addAttribute("userBadgeIds", userBadgeIds);
            model.addAttribute("userBadges", userBadges); // 필요하면 그대로 추가
            model.addAttribute("badges", badges);
            model.addAttribute("userImageId", userImageId);
            model.addAttribute("user", user);
        } catch (DBNotFoundException e) {
            model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
        }
    }
    return "user/mypage-badge";
}


        @GetMapping("/donation/detail/{donationId}")
        public String donationDetail(
                HttpServletRequest request,
                @PathVariable("donationId") Long donationId,
                Model model
        ) {
            HttpSession session = request.getSession(false);
            Long userId = (session != null) ? (Long) session.getAttribute("loginUserId") : null;

            if (userId != null) {
                try {
                    // 1. 사용자 정보 조회
                    UserDTO user = userService.readUserById(userId);

                    // 2. 특정 도네이션 정보 가져오기
                    MaterialDonationDTO donation = materialDonationService.getDonationByDonationId(donationId);

                    if (donation == null) {
                        model.addAttribute("error", "해당 도네이션 정보를 찾을 수 없습니다.");
                        return "user/mypage-donation-detail";
                    }

                    // 3. 도네이션과 연결된 캠페인 정보 가져오기
                    CampaignDTO campaign = donation.getCampaign();
                    String imageUrl = (campaign != null && campaign.getPhoto() != null)
                            ? imageService.getImageUrl(campaign.getPhoto().getImageId())
                            : null;

                    // 4. 캠페인의 목표량 및 후원량 계산
                    int goal = (campaign != null)
                            ? campaignGoalService.getCampaignGoalByCampaignId(campaign.getCampaignId()).get(0)
                                    .getQuantityRequired()
                            : 1; // 목표량이 없으면 기본값 1 설정 (0 나눗셈 방지)

                    int donationsum = materialDonationService
                            .getMaterialDonationsByCampaignId(campaign.getCampaignId())
                            .stream()
                            .mapToInt(MaterialDonationDTO::getQuantity)
                            .sum();

                    double achievement = (double) donationsum / goal * 100; // 백분율 변환
                    achievement = Math.round(achievement);

                    // 5. 리워드 정보 가져오기
                    RewardDeliveryDTO reward = rewardDeliveryService
                            .getRewardDeliveryByDonationId(donation.getDonationId());
                    
                    String rewardName = rewardDeliveryService.getRewardNameByDonationId(donation.getDonationId());

                    // 6. 사용자 프로필 이미지 설정
                    String userImageId = "https://imagedelivery.net/sXWs4txHKON-dqRmy35ZtA/"
                            + user.getPhoto().getImageId() + "/public";

                    // 7. 모델에 값 추가
                    model.addAttribute("donation", donation);
                    model.addAttribute("campaign", campaign);
                    model.addAttribute("imageUrl", imageUrl);
                    model.addAttribute("achievement", achievement);
                    model.addAttribute("reward", reward);
                    model.addAttribute("rewardName", rewardName);
                    model.addAttribute("user", user);
                    model.addAttribute("userImageId", userImageId);

                } catch (DBNotFoundException e) {
                    model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
                }
            }

            return "user/mypage-donation-detail";
        }

        @PostMapping("/donation/detail/{donationId}")
        @ResponseBody
        public Map<String, String> deliveryTrack(
                @PathVariable("donationId") Long donationId,
                @RequestParam("courierId") String courierId,
                @RequestParam("trackingNumber") String trackingNumber,
            Model model
        ) {
            String status = deliveryService.trackDelivery(courierId, trackingNumber);
            Map<String, String> response = new HashMap<>();
            response.put("status", status);

            return response;
        }


        
        @GetMapping("/like")
        public String like(
                @RequestParam(value = "sort", required = false, defaultValue = "latest") String sort,
                HttpServletRequest request, Model model) {
            HttpSession session = request.getSession(false);
            Long userId = (Long) session.getAttribute("loginUserId");

            if (userId != null) {
                try {
                    UserDTO user = userService.readUserById(userId);
                    List<LikeDTO> likes = likeService.getLikeByUserUserId(userId);
                    List<CampaignGoalDTO> campaignGoals = new ArrayList<>();
                    int donationSum = 0;

                    // 좋아요한 항목들을 크리에이터와 캠페인으로 분류합니다.
                    List<LikeDTO> creatorLikes = likes.stream()
                            .filter(like -> like.getCreator() != null)
                            .collect(Collectors.toList());
                    List<LikeDTO> donationLikes = likes.stream()
                            .filter(like -> like.getCampaign() != null)
                            .collect(Collectors.toList());

                    // donationLikes에 포함된 모든 캠페인을 중복 제거하여 가져옵니다.
                    List<CampaignDTO> campaigns = donationLikes.stream()
                            .map(LikeDTO::getCampaign)
                            .distinct()
                            .collect(Collectors.toList());

                    // 최신순 (createdDate 내림차순) 또는 마감임박순 (endDate 오름차순)으로 정렬
                    if ("latest".equals(sort)) {
                        campaigns.sort(Comparator.comparing(CampaignDTO::getCreatedDate).reversed());
                    } else if ("deadline".equals(sort)) {
                        campaigns.sort(Comparator.comparing(CampaignDTO::getEndDate));
                    }

                    // creatorLikes에 포함된 모든 크리에이터를 중복 제거하여 가져옵니다.
                    List<CreatorDTO> creators = creatorLikes.stream()
                            .map(LikeDTO::getCreator)
                            .distinct()
                            .collect(Collectors.toList());

                    // 각 캠페인의 이미지 URL 추출
                    List<String> campaignImageUrls = new ArrayList<>();
                    for (CampaignDTO campaign : campaigns) {
                        if (campaign != null && campaign.getPhoto() != null) {
                            campaignImageUrls.add(imageService.getImageUrl(campaign.getPhoto().getImageId()));
                            List<CampaignGoalDTO> campaignGoal = campaignGoalService.getCampaignGoalByCampaignId(campaign.getCampaignId());
                            campaignGoals.add(campaignGoal.get(0));
                            List<MaterialDonationDTO> materialDonationsList = materialDonationService
                                    .getMaterialDonationsByCampaignId(campaign.getCampaignId());
                            for (MaterialDonationDTO donation : materialDonationsList) {
                                donationSum += donation.getQuantity();
                            }
                        } else {
                            campaignImageUrls.add(null);
                        }
                    }

                    // 각 크리에이터의 이미지 URL 추출
                    List<String> creatorImageUrls = new ArrayList<>();
                    for (CreatorDTO creator : creators) {
                        if (creator != null && creator.getProImgUrl() != null) {
                            creatorImageUrls.add(imageService.getImageUrl(creator.getProImgUrl().getImageId()));
                        } else {
                            creatorImageUrls.add(null);
                        }
                    }

                    // 사용자 프로필 이미지 URL 생성
                    String userImageId = "https://imagedelivery.net/sXWs4txHKON-dqRmy35ZtA/"
                            + user.getPhoto().getImageId() + "/public";

                    // 각 좋아요 항목에서 크리에이터 또는 캠페인별 likeId 매핑
                    Map<Long, Long> creatorLikeIdMap = new HashMap<>();
                    for (LikeDTO like : creatorLikes) {
                        if (like.getCreator() != null) {
                            creatorLikeIdMap.put(like.getCreator().getCreatorId(), like.getLikeId());
                        }
                    }

                    Map<Long, Long> campaignLikeIdMap = new HashMap<>();
                    for (LikeDTO like : donationLikes) {
                        if (like.getCampaign() != null) {
                            campaignLikeIdMap.put(like.getCampaign().getCampaignId(), like.getLikeId());
                        }
                    }
                    List<MaterialDonationDTO> materialDonations = materialDonationService.getMaterialDonationsByUserId(userId);
                    int donationCount = materialDonations.size();

                    if ("latest".equals(sort)) {
                        campaigns.sort(Comparator.comparing(CampaignDTO::getCreatedDate).reversed());
                    } else if ("deadline".equals(sort)) {
                        campaigns.sort(Comparator.comparing(CampaignDTO::getEndDate));
                    }

                    Map<Long, Integer> donationSumByCampaign = materialDonations.stream()
                        .collect(Collectors.groupingBy(
                            donation -> donation.getCampaign().getCampaignId(),
                            Collectors.summingInt(MaterialDonationDTO::getQuantity)
                            ));

                    double achievement = (double) donationSum / campaignGoals.get(0).getQuantityRequired() * 100;
                    achievement = Math.round(achievement);

                    
                    // 모델에 값 추가
                    model.addAttribute("donationSumByCampaign", donationSumByCampaign);
                    model.addAttribute("achievement", achievement);
                    model.addAttribute("likes", likes);
                    model.addAttribute("creatorLikesCount", creatorLikes.size()); // 크리에이터 좋아요 수
                    model.addAttribute("donationCount", donationCount); // 캠페인 좋아요 수
                    model.addAttribute("user", user);
                    model.addAttribute("userImageId", userImageId);
                    model.addAttribute("campaigns", campaigns);
                    model.addAttribute("campaignImageUrls", campaignImageUrls);
                    model.addAttribute("campaignGoals", campaignGoals);
                    model.addAttribute("creators", creators);
                    model.addAttribute("creatorImageUrls", creatorImageUrls);
                    model.addAttribute("creatorLikeIdMap", creatorLikeIdMap);
                    model.addAttribute("campaignLikeIdMap", campaignLikeIdMap);
                    model.addAttribute("sort", sort); // 현재 정렬 기준을 전달

                } catch (DBNotFoundException e) {
                    model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
                }
            }

            return "user/mypage-like";
        }






    // 충돌로 인한 주석처리 - 필요시 다시 검토 필요
    // @PostMapping("/userUpdate")
    // public String postMethodName(
    // HttpServletRequest request,
    // @ModelAttribute UserDTO userDTO,
    // RedirectAttributes rttr
    // ) {
    // log.info(userDTO.toString());
    // String oauthId = request.getUserPrincipal().getName();
    // UserDTO user = UserDTO.toDTO(userService.findUserByIdentity(oauthId));
    // userDTO.setUserId(user.getUserId());
    // userService.userUpdate(userDTO);
    // log.info(user.toString());
    // return "redirect:/user";
    // }

//    @GetMapping("/badge")
//    public String badge() {
//        return "user/mypage-badge";
//    }

    @GetMapping("/donation/detail")
    public String alarmDetail() {
        return "user/mypage-donation-detail";
    }

    // 로그인 관련

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
            HttpSession session, Model model) {
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            session.setAttribute("redirectUrl", redirectUrl);
        }
        return "login/login";
    }

    @GetMapping("/join")
    public String joinDetailPage(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null) {

            // 세션에서 카카오 정보 꺼내기
            String identity = (String) session.getAttribute("identity");
            String nickname = (String) session.getAttribute("nickname");
            String email = (String) session.getAttribute("email");
            String find = (String) session.getAttribute("find");


            // 모델에 담아 뷰로 전달
            model.addAttribute("identity", identity);
            model.addAttribute("nickname", nickname);
            model.addAttribute("email", email);
            model.addAttribute("find", find);

        }
        return "login/joindetail";
    }

    @PostMapping("/joinProc")
    public String joinDetailSubmit(HttpServletRequest request, @RequestParam("check") int check,
            @RequestParam("address") String address,
            @RequestParam("phone") String phone,
            @RequestParam("intro") String intro) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            // 세션이 없으면 에러 처리
            return "redirect:/error";
        }
        String kakaoId = (String) session.getAttribute("identity");
        String nickname = (String) session.getAttribute("nickname");
        String email = (String) session.getAttribute("email");
        String find = (String) session.getAttribute("find");

        // DB 저장 (UserDTO 만들거나 직접)
        UserDTO userDTO = new UserDTO();
        userDTO.setOauthId(kakaoId);
        userDTO.setUserName(nickname);
        userDTO.setEmail(email);
        userDTO.setLoginType(find);
        userDTO.setIsMarketed(check);
        userDTO.setUserAddress(address);
        userDTO.setPhoneNumber(phone);
        userDTO.setIntro(intro);

        // userService.add(...) 저장
        UserEntity savedUser = userService.saveUser(userDTO);

        // 가입 완료 후, 세션에 로그인 정보 저장
        session.setAttribute("loginUserId", savedUser.getUserId());

        chatRoomService.createChatRoom(savedUser.getUserId(), 0L);

        // 메인 페이지로 리다이렉트
        return "redirect:/";


    }

}
