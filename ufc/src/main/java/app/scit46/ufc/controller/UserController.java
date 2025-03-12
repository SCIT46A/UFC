package app.scit46.ufc.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import app.scit46.ufc.dto.LikeDTO;
import app.scit46.ufc.dto.MaterialDonationDTO;
import app.scit46.ufc.dto.UserBadgeDTO;
import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.reward.RewardDeliveryDTO;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.entity.campaign.CampaignReviewEntity;
import app.scit46.ufc.exception.DBNotFoundException;
import app.scit46.ufc.service.BadgeService;
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
                    }else{
                        imageUrls.add(imageService.getImageUrl(user.getPhoto().getImageId()));
                    }
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
                            CampaignDTO campaign = campaignService.getCampaignById(reviewEntity.getCampaignedBy().getCampaignId());
                            campaigns.add(campaign);
                            imageUrls.add(imageService.getImageUrl(reviewEntity.getCampaignedBy().getPhoto().getImageId()));
                        }

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
        public String edit (HttpServletRequest request, Model model){
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
        public String donation(
                HttpServletRequest request
                , Model model
                ){
            HttpSession session = request.getSession(false);
            Long userId = null;

            if (session != null) {
                userId = (Long) session.getAttribute("loginUserId");
                if (userId != null) {
                    try {
                        UserDTO user = userService.readUserById(userId);



                        List<MaterialDonationDTO> materialDonations = materialDonationService
                                .getMaterialDonationsByUserId(userId);

                        List<CampaignDTO> campaigns = new ArrayList<>();
                        List<String> imageUrls = new ArrayList<>();
                        List<RewardDeliveryDTO> rewardDeliveries = new ArrayList<>();
                        int goal, donationsum = 0;
                        List<Double> isAchived = new ArrayList<>();        
                        
                        List<Double> isAchivedList = new ArrayList<>();
                        List<String> rewardNameList = new ArrayList<>();
                        String rewardName = null;

                        for (MaterialDonationDTO materialDonation : materialDonations) {
                            CampaignDTO campaign = materialDonation.getCampaign();
                            if (campaign != null && campaign.getPhoto() != null) {
                                campaigns.add(campaign);
                                imageUrls.add(imageService.getImageUrl(campaign.getPhoto().getImageId()));

                                goal = campaignGoalService.getCampaignGoalByCampaignId(campaign.getCampaignId()).get(0).getQuantityRequired();
                                List<MaterialDonationDTO> materialDonationsList = materialDonationService
                                        .getMaterialDonationsByCampaignId(campaign.getCampaignId());

                                donationsum = 0; // 후원 개수 초기화
                                for (MaterialDonationDTO donation : materialDonationsList) {
                                    donationsum += donation.getQuantity();
                                }
                                
                                double achievement = (double) donationsum / goal * 100; // 백분율 변환
                                isAchivedList.add(achievement); // 리스트에 추가

                                rewardName = rewardDeliveryService
                                        .getRewardNameByDonationId(materialDonation.getDonationId());
                                rewardNameList.add(rewardName);


                            }
                        }   
                        Map<String, String> statusMap = new HashMap<>();
                        statusMap.put("approved", "확인되었어요 🙂");
                        statusMap.put("rejected", "거절되었어요 🙁");
                        statusMap.put("pending", "검토중이에요!");
                        model.addAttribute("statusMap", statusMap);


                        model.addAttribute("isAchivedList", isAchivedList);
                        model.addAttribute("rewardNameList", rewardNameList);

                        System.out.println("후원 내역 개수: " + materialDonations.size());
                        //문제없음 5개로 나옴
                        
                        // List<RewardDTO> rewards = new ArrayList<>();
                        String userImageId = "https://imagedelivery.net/sXWs4txHKON-dqRmy35ZtA/"
                                + user.getPhoto().getImageId() + "/public";

                        // int totalPages = paging.getTotalPages();



                        // model.addAttribute("totalPages", totalPages);
                        //캠페인 달성 성공 여부
                        model.addAttribute("isAchived", isAchived);
                        //캠페인 이미지
                        model.addAttribute("imageUrls", imageUrls);
                        //캠페인 정보
                        model.addAttribute("campaigns", campaigns);
                        //유저 후원 개수
                        model.addAttribute("donationCount", materialDonations.size());
                        //유저 후원 내역
                        model.addAttribute("materialDonations", materialDonations);
                        //유저 정보
                        model.addAttribute("user", user);
                        //유저 프로필 이미지
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
            return "logout";
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
            HttpServletRequest request, 
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "6") int size
            ){
        HttpSession session = request.getSession(false);
        Long userId = (Long) session.getAttribute("loginUserId");
        if (userId != null) {
            try {

                UserDTO user = userService.readUserById(userId);
                
                List<LikeDTO> likes = likeService.getLikeByUserUserId(userId);
                // 좋아요한 캠페인만 페이징 처리
                Page<CampaignDTO> paging = likeService.getLikedCampaignsByUserId(userId, page, size);
                List<CampaignDTO> campaigns = paging.getContent();

                List<String> imageUrls = new ArrayList<>();
                String userImageId = "https://imagedelivery.net/sXWs4txHKON-dqRmy35ZtA/"
                        + user.getPhoto().getImageId() + "/public";
                
                // 각 캠페인의 이미지 URL 추출 (캠페인에 photo가 있는 경우 100%있음)
                for (CampaignDTO campaign : campaigns) {
                    if (campaign != null && campaign.getPhoto() != null) {
                        imageUrls.add(imageService.getImageUrl(campaign.getPhoto().getImageId()));
                    } else {
                        imageUrls.add(null);
                    }
                }

                
                model.addAttribute("likes", likes);
                model.addAttribute("user", user);
                model.addAttribute("userImageId", userImageId);
                model.addAttribute("campaigns", campaigns);
                model.addAttribute("imageUrls", imageUrls);
                model.addAttribute("campaignCount", paging.getTotalElements());
                model.addAttribute("paging", paging);

            } catch (DBNotFoundException e) {
                model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
            }
        }
        return "user/mypage-like";
    }





        // 로그인 관련

        @GetMapping("/login")
        public String loginPage(@RequestParam(value = "redirectUrl", required = false) String redirectUrl, HttpSession session, Model model) {
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                session.setAttribute("redirectUrl", redirectUrl);
            }
            return "login/login";
        }


        @GetMapping("/join")
        public String joinDetailPage (HttpServletRequest request, Model model){
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
            return "/login/joindetail";
        }

        @PostMapping("/joinProc")
        public String joinDetailSubmit (HttpServletRequest request,@RequestParam("check") int check,
        @RequestParam("address") String address,
        @RequestParam("phone") String phone,
        @RequestParam("intro") String intro){
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

            // 메인 페이지로 리다이렉트
            return "redirect:/";
        }


}
