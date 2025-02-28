package app.scit46.ufc.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import app.scit46.ufc.dto.LikeDTO;
import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.campaign.CampaignReviewDTO;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.exception.DBNotFoundException;
import app.scit46.ufc.service.CampaignReviewService;
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
    private final MaterialDonationService materialDonationService;
    private final CampaignService campaignService;
    private final CampaignReviewService campaignReviewService;
    private final LikeService likeService;
    private final ImageService imageService;

    // 유저 기본페이지 조회
    @GetMapping({"/",""})
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
                    imageUrls.add(imageService.getImageUrl(user.getPhoto().getImageId()));
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

    @GetMapping("/like")
    public String like(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        Long userId = null;
        if (session != null) {
            userId = (Long) session.getAttribute("loginUserId");
            List<LikeDTO> like = likeService.getLikesByUserId(userId);
            model.addAttribute("like", like);
        }
        return "user/mypage-like";
    }


    @GetMapping("/review")
    public String review(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        Long userId = null;
        
        if (session != null) {
            userId = (Long) session.getAttribute("loginUserId");
            if (userId != null) {
                try {
                    UserDTO user = userService.readUserById(userId);
                    List<CampaignReviewDTO> list = campaignReviewService.getCampaignReviewsByUserId(userId);
                    List<CampaignDTO> campaigns = new ArrayList<>();
                    List<String> imageUrls = new ArrayList<>();
                    for (CampaignReviewDTO review : list) {
                        CampaignDTO campaign = campaignService.getCampaignById(review.getCampaignedBy().getCampaignId());
                        campaigns.add(campaign);
                        imageUrls.add(imageService.getImageUrl(review.getCampaignedBy().getPhoto().getImageId()));
                    }
                    model.addAttribute("imageUrls", imageUrls);
                    model.addAttribute("campaignReviews", list);
                    model.addAttribute("campaigns", campaigns); // 캠페인 정보 추가
                    model.addAttribute("user", user);
                    model.addAttribute("reviewCount", list.size());
                } catch (DBNotFoundException e) {
                    // 사용자 정보를 찾을 수 없는 경우 처리
                    model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
                }
            }

        }
        return "user/mypage-review";
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
            HttpServletRequest request
            , @ModelAttribute UserDTO userDTO
            , RedirectAttributes rttr
    ) {
        HttpSession session = request.getSession(false);
        userDTO.setUserId((Long) session.getAttribute("loginUserId"));
        userService.userUpdate(userDTO);
        

        return "redirect:/user";
    }
    
    


    @GetMapping("/badge")
    public String badge() {
        return "user/mypage-badge";
    }







    @GetMapping("/donation/detail")
    public String alarmDetail() {
        return "user/mypage-donation-detail";
    }



    
//    로그인 관련
    
    @GetMapping("/login")
    public String login() {
        return "login/login";
    }

    @GetMapping("/join")
    public String joinDetailPage(HttpServletRequest request, Model model
    ) {
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
    public String joinDetailSubmit(HttpServletRequest request, @RequestParam("check") int check ,
                                   @RequestParam("address") String address,
                                   @RequestParam("phone") String phone,
                                   @RequestParam("intro") String intro
                                   )
    {
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

