package app.scit46.ufc.controller;


import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import app.scit46.ufc.dto.LikeDTO;
import app.scit46.ufc.dto.MaterialDonationDTO;
import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.entity.campaign.CampaignReviewEntity;
import app.scit46.ufc.exception.DBNotFoundException;
import app.scit46.ufc.service.campaign.CampaignReviewService;
import app.scit46.ufc.service.LikeService;
import app.scit46.ufc.service.MaterialDonationService;
import app.scit46.ufc.service.UserService;
import app.scit46.ufc.service.campaign.CampaignService;
import app.scit46.ufc.service.cloudflare.ImageService;
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
    @GetMapping("/donation")
    public String donation(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        Long loginUserId = null;




    @GetMapping("/review")
    public String review(HttpServletRequest request,
                     Model model,
                     @RequestParam(value = "page", defaultValue = "0") int page,
                     @RequestParam(value = "size", defaultValue = "5") int size) {
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
                String userImageId = "https://imagedelivery.net/sXWs4txHKON-dqRmy35ZtA/" 
                        + user.getPhoto().getImageId() + "/public";
                
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
    Long loginUserId = null;

    if (session != null) {
        loginUserId = (Long) session.getAttribute("loginUserId");
        if (loginUserId != null) {
            try {
                UserDTO user = userService.readUserById(loginUserId);
                List<MaterialDonationDTO> materialDonations = materialDonationService
                        .getMaterialDonationsByUserId(loginUserId);
                System.out.println("후원 내역 개수: " + materialDonations.size());
                //문제없음 5개로 나옴
                List<CampaignDTO> campaigns = new ArrayList<>();
                List<String> imageUrls = new ArrayList<>();
                // List<RewardDTO> rewards = new ArrayList<>();
                for (MaterialDonationDTO materialDonation : materialDonations) {
                    CampaignDTO campaign = materialDonation.getCampaign();
                    if (campaign != null && campaign.getPhoto() != null) {
                        campaigns.add(campaign);
                        imageUrls.add(imageService.getImageUrl(campaign.getPhoto().getImageId()));
                        // rewards.add(materialDonation.getCampaign().getRewards());
                    } else {
                        System.out.println("⭕⭕⭕⭕⭕⭕⭕⭕⭕⭕⭕⭕⭕⭕⭕⭕⭕⭕⭕⭕⭕⭕⭕⭕⭕🚨 캠페인 정보 없음: " + materialDonation.getDonationId()); // 로그 추가
                    }
                }
                model.addAttribute("imageUrls", imageUrls);
                model.addAttribute("campaigns", campaigns);
                model.addAttribute("donationCount", materialDonations.size());
                model.addAttribute("materialDonations", materialDonations);
                model.addAttribute("user", user);
            } catch (DBNotFoundException e) {
                model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
            }
        }
    }

    return "user/mypage-donation";
}



    // 회원탈퇴(status 1로 변경)
    @GetMapping("/delete")
    public String delete(
            HttpSession session,
            @RequestParam(name = "userId") Long userId) {
        userService.delete(userId);
        session.removeAttribute("loginUserId");
        return "logout";
    }

    
    @PostMapping("/userUpdate")
    public String postMethodName(
            HttpServletRequest request, @ModelAttribute UserDTO userDTO, RedirectAttributes rttr) {
        HttpSession session = request.getSession(false);
        userDTO.setUserId((Long) session.getAttribute("loginUserId"));
        userService.userUpdate(userDTO);

        return "redirect:/user";
    }
      
// 충돌로 인한 주석처리 - 필요시 다시 검토 필요
// @PostMapping("/userUpdate")
// public String postMethodName(
//         HttpServletRequest request,
//         @ModelAttribute UserDTO userDTO,
//         RedirectAttributes rttr
// ) {
//     log.info(userDTO.toString());
//     String oauthId = request.getUserPrincipal().getName();
//     UserDTO user = UserDTO.toDTO(userService.findUserByIdentity(oauthId));
//     userDTO.setUserId(user.getUserId());
//     userService.userUpdate(userDTO);
//     log.info(user.toString());
//     return "redirect:/user";
// }

    

    @GetMapping("/badge")
    public String badge( ) {
        return "user/mypage-badge";
    }





    @GetMapping("/donation/detail")
    public String alarmDetail() {
        return "user/mypage-donation-detail";
    }


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
                    model.addAttribute("user", user);
                } catch (DBNotFoundException e) {
                    // 사용자 정보를 찾을 수 없는 경우 처리
                    model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
                }
            }
        }
        return "user/mypage-profile-edit";
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
        return "/login/joindetail";
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

        // 메인 페이지로 리다이렉트
        return "redirect:/";
    }

}
