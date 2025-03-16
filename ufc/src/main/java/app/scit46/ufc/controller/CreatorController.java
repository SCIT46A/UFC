package app.scit46.ufc.controller;

import java.net.http.HttpRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import app.scit46.ufc.dto.CreatorDTO;
import app.scit46.ufc.dto.ImageUrlDTO;
import app.scit46.ufc.dto.SearchResultDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.custom.CreatorCreateDTO;
import app.scit46.ufc.service.CreatorService;
import app.scit46.ufc.service.ImageUrlService;
import app.scit46.ufc.service.SearchService;
import app.scit46.ufc.service.UserService;
import app.scit46.ufc.service.campaign.CampaignService;
import app.scit46.ufc.service.cloudflare.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/creator") // 🔹 모든 URL이 "/creator"로 시작하도록 설정
@RequiredArgsConstructor
public class CreatorController {

    private final UserService userService;
    private final CreatorService creatorService;
    private final ImageService imageService;
    private final SearchService searchService;

    /** 🔹 [GET] 창작가 개설 페이지 출력 */
    @GetMapping("/create")
    public String getCreatorPage(Model model) {
        model.addAttribute("message", "창작가 개설 페이지입니다!");
        return "creator/creator-create"; // ✅ ".html" 붙이지 않음!
    }

    /** 🔹 [POST] 입력값을 DB에 저장 */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<String> createCreator(@RequestBody CreatorCreateDTO creatorCreateDTO,
            HttpServletRequest httpServletRequest) {
        String OAuthId = httpServletRequest.getUserPrincipal().getName();

        // 값 확인
        System.out.println("📥 받은 요청 데이터: " + creatorCreateDTO);
        System.out.println("받은 bRegistDate: " + creatorCreateDTO.getBRegistDate());

        // 주소 기본값 설정
        if (creatorCreateDTO.getAddress() == null || creatorCreateDTO.getAddress().isEmpty()) {
            creatorCreateDTO.setAddress("주소 없음");
        }

        // 사업자 등록일 기본값 설정
        if (creatorCreateDTO.getBRegistDate() == null) {
            creatorCreateDTO.setBRegistDate(LocalDate.now());
        }
        // creatorService.createCreator(creatorCreateDTO, OAuthId);
        creatorService.createCreator(creatorCreateDTO, httpServletRequest.getUserPrincipal().getName());
        return ResponseEntity.ok("창작가가 성공적으로 저장되었습니다!");
    }

    private String getImageUrl(ImageUrlDTO image) {
        if (image == null || image.getImageId() == null) {
            return "/images/default-profile.png"; // 기본 이미지 경로 (적절하게 변경 가능)
        }
        return "/uploads/" + image.getImageId(); // 실제 업로드된 이미지 URL
    }

    /** 🔹 [GET] 특정 창작가 정보 불러오기 */
    @GetMapping("/campaign")
    public String getCreatorCampaignPage(Model model, HttpServletRequest request) {
        String OAuthId = request.getUserPrincipal().getName(); // 현재 로그인한 사용자 ID 가져오기
        CreatorDTO creator = creatorService.findCreatorByUser(OAuthId); // DB에서 창작가 정보 가져오기

        if (creator == null) {
            model.addAttribute("errorMessage", "창작가 정보를 찾을 수 없습니다.");
            return "error"; // 오류 페이지로 이동
        }

        // ✅ `imageId`를 `URL`로 변환하여 모델에 추가
        model.addAttribute("creator", creator);
        model.addAttribute("profileImgUrl", imageService.getImageUrl(creator.getProImgUrl().getImageId())); // ✅ 변환된 URL
                                                                                                            // 사용
        model.addAttribute("backImgUrl", imageService.getImageUrl(creator.getBackImgUrl().getImageId())); // ✅ 변환된 URL
                                                                                                          // 사용

        return "creator/creator-campaign"; // 창작가 캠페인 페이지로 이동
    }

    /** 🔹 [GET] 창작가 수정 페이지 */
    @GetMapping("/edit")
    public String editProfile(Model model, HttpServletRequest httpServletRequest) {
        String OAuthId = httpServletRequest.getUserPrincipal().getName();
        CreatorDTO creator = creatorService.findCreatorByUser(OAuthId);
        System.out.println(creator);
        String profileImg = imageService.getImageUrl(creator.getProImgUrl().getImageId());
        String backImg = imageService.getImageUrl(creator.getBackImgUrl().getImageId());
        System.out.printf("profile : %s, back : %s", profileImg, backImg);
        model.addAttribute("profileImgUrl", profileImg);
        model.addAttribute("backImgUrl", backImg);
        model.addAttribute("message", "창작가 프로필 수정 페이지입니다!");
        model.addAttribute("creator", creator);
        return "creator/creator-edit";
    }

    /** 🔹 [POST] 창작가 프로필 수정 */
    @PatchMapping("/update") // 부분 업데이트는 PATCH가 더 적절하다고 함
    @ResponseBody
    public ResponseEntity<String> updateCreator(@RequestBody CreatorCreateDTO creatorDTO,
            HttpServletRequest httpServletRequest) {
        try {
            // 유저 ID를 이용하여 Creator 조회
            CreatorDTO creator = creatorService.findCreatorByUser(httpServletRequest.getUserPrincipal().getName());
            // creator.setCreatorId(creator.getCreatorId());
            System.out.println("📥 수정 데이터 수신: " + creatorDTO.toString());
            // creatorDTO의 필드 중 null로 넘어오는 것이 db에도 반영이 되면 안됨
            // 만약 반영이 되면 creatorId에 creatorDTO의 null이 아닌 필드의 내용을 set으로 대체하는게 좋을 것으로 판단됨
            // 서비스에서 업데이트 실행
            creatorDTO.setId(creator.getCreatorId());
            boolean isUpdated = creatorService.updateCreator(creator, creatorDTO);

            if (!isUpdated) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("업데이트 실패: 존재하지 않는 사용자");
            }
            return ResponseEntity.ok("프로필이 성공적으로 수정되었습니다!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류 발생: " + e.getMessage());
        }
    }

    /** 🔹 [GET] 캠페인 페이지 */
    @GetMapping("/campaign/{id}")
    public String getCreatorCampaignPage(@PathVariable("id") Long id, Model model) {
        CreatorDTO creator = creatorService.getCreator(id);

        if (creator == null) {
            // 기본 데이터를 설정해서 NullPointerException 방지
            creator = new CreatorDTO();
            creator.setCreatorId(0L);
            creator.setCompanyName("기본 상호명");
            creator.setIntro("기본 소개글");
        }

        model.addAttribute("creator", creator);
        return "creator/creator-campaign";
    }

    /** 🔹 [GET] 기존 데이터 불러오기 */
    @GetMapping("/edit/data")
    @ResponseBody
    public ResponseEntity<CreatorDTO> getCreatorEditData(HttpServletRequest httpServletRequest) {
        String OAuthId = httpServletRequest.getUserPrincipal().getName();
        CreatorDTO creator = creatorService.findCreatorByUser(OAuthId);
        return ResponseEntity.ok(creator);

    }

    /** ✅ 현재 로그인한 유저의 진행 중인 캠페인 목록 */
    // @GetMapping("/active") // ✅ URL 수정 (active를 고정값으로 설정)
    // public ResponseEntity<List<CampaignDTO>>
    // getActiveCampaignsByCreator(Principal principal) {
    // try {
    // String userId = principal.getName(); // 로그인한 유저의 OAuth ID 가져오기
    // List<CampaignDTO> campaigns =
    // CampaignService.getActiveCampaignsByCreator(userId);
    // return ResponseEntity.ok(campaigns);
    // } catch (Exception e) {
    // e.printStackTrace();
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    // }
    // }

    /** ✅ 현재 로그인한 창작가의 진행 중인 캠페인 목록 가져오기 */
    // @GetMapping("/campaign/active")
    // public ResponseEntity<List<CampaignDTO>> getCreatorCampaigns(Principal
    // principal) {
    // try {
    // String userId = principal.getName(); // 로그인한 유저 ID
    // List<CampaignDTO> campaigns = creatorService.getCreatorCampaigns(userId);
    // return ResponseEntity.ok(campaigns);
    // } catch (Exception e) {
    // e.printStackTrace();
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    // }
    // }

    @Autowired
    private CampaignService campaignService;

    // @GetMapping("/campaign/active")
    // @ResponseBody
    // public ResponseEntity<List<CampaignDTO>> getCreatorActiveCampaigns(Principal
    // principal) {
    // try {
    // String oauthId = principal.getName();
    // Long creatorId = campaignService.getCreatorIdByOauthId(oauthId);

    // if (creatorId == null) {
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    // }

    // // ✅ 진행 중인 캠페인 가져오기
    // List<CampaignDTO> campaigns =
    // campaignService.getActiveCampaignsByCreator(creatorId);
    // return ResponseEntity.ok(campaigns);
    // } catch (Exception e) {
    // e.printStackTrace();
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    // }
    // }
    @GetMapping("/campaign/active")
    @ResponseBody
    public ResponseEntity<List<SearchResultDTO>> searchActive(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "3") int limit) {
        HttpSession session = request.getSession(false);
        Long loginUserId = (session != null) ? (Long) session.getAttribute("loginUserId") : null;
        Long creatorId = campaignService.getCreatorIdByOauthId(userService.findById(loginUserId).getOauthId());
        List<SearchResultDTO> results = searchService.searchAllActiveByCreator(creatorId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/campaign/finished")
    @ResponseBody
    public ResponseEntity<List<SearchResultDTO>> searchFinished(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "3") int limit) {
        HttpSession session = request.getSession(false);
        Long loginUserId = (session != null) ? (Long) session.getAttribute("loginUserId") : null;
        Long creatorId = campaignService.getCreatorIdByOauthId(userService.findById(loginUserId).getOauthId());
        List<SearchResultDTO> results = searchService.searchAllFinishedByCreator(creatorId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/campaign/rejected")
    @ResponseBody
    public ResponseEntity<List<SearchResultDTO>> searchAppointed(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "3") int limit) {
        HttpSession session = request.getSession(false);
        Long loginUserId = (session != null) ? (Long) session.getAttribute("loginUserId") : null;
        Long creatorId = campaignService.getCreatorIdByOauthId(userService.findById(loginUserId).getOauthId());
        List<SearchResultDTO> results = searchService.searchAllAppointedByCreator(creatorId);
        return ResponseEntity.ok(results);
    }

    // ✅ 종료된 캠페인 조회 API
    // @GetMapping("/campaign/finished")
    // @ResponseBody
    // public ResponseEntity<List<CampaignDTO>>
    // getCreatorFinishedCampaigns(Principal principal) {
    // try {
    // String oauthId = principal.getName();
    // Long creatorId = campaignService.getCreatorIdByOauthId(oauthId);

    // if (creatorId == null) {
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    // }

    // // ✅ 종료된 캠페인 가져오기
    // List<CampaignDTO> campaigns =
    // campaignService.getFinishedCampaignsByCreator(creatorId);
    // return ResponseEntity.ok(campaigns);
    // } catch (Exception e) {
    // e.printStackTrace();
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    // }
    // }

    // 거절당한 캠페인
    // @GetMapping("/campaign/rejected")
    // @ResponseBody
    // public ResponseEntity<List<CampaignDTO>> getRejectedCampaigns(Principal
    // principal) {
    // try {
    // String oauthId = principal.getName();
    // Long creatorId = campaignService.getCreatorIdByOauthId(oauthId);

    // if (creatorId == null) {
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    // }

    // // ✅ 거절당한 캠페인 가져오기 (본인 소유 캠페인만)
    // List<CampaignDTO> campaigns =
    // campaignService.getRejectedCampaignsByCreator(creatorId);

    // // ✅ 본인 캠페인 확인
    // if (campaigns.isEmpty()) {
    // return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 접근 권한 없음
    // }

    // return ResponseEntity.ok(campaigns);
    // } catch (Exception e) {
    // e.printStackTrace();
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    // }
    // }
}
