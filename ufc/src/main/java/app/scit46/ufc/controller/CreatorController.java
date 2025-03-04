package app.scit46.ufc.controller;

import java.net.http.HttpRequest;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import app.scit46.ufc.dto.CreatorDTO;
import app.scit46.ufc.dto.custom.CreatorCreateDTO;
import app.scit46.ufc.service.CreatorService;
import app.scit46.ufc.service.cloudflare.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/creator") // 🔹 모든 URL이 "/creator"로 시작하도록 설정
@RequiredArgsConstructor
public class CreatorController {

    private final CreatorService creatorService;
    private final ImageService imageService;

    /** 🔹 [GET] 창작가 개설 페이지 출력 */
    @GetMapping("/create")
    public String getCreatorPage(Model model) {
        model.addAttribute("message", "창작가 개설 페이지입니다!");
        return "creator/creator-create"; // ✅ ".html" 붙이지 않음!
    }

    /** 🔹 [GET] 창작가 캠페인 페이지 출력 */
    /*
     * @GetMapping("/campaign")
     * public String getCreatorCampaignPage(Model model) {
     * System.out.println("✅ [LOG] 창작가 캠페인 페이지 요청됨!"); // 🚀 요청 확인용 로그
     * return "creator/creator-campaign";
     * }
     */

    // /** 🔹 [GET] 창작가 프로필 수정 페이지 출력 */
    // @GetMapping("/edit")
    // public String getCreatorEditPage(Model model) {
    // model.addAttribute("imageList", imageService.getImageUrl(null))
    // model.addAttribute("message", "창작가 프로필 수정 페이지입니다!");
    // return "creator/creator-edit"; // ✅ ".html" 붙이지 않음!
    // }

    /** 🔹 [POST] 입력값을 DB에 저장 */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<String> createCreator(@RequestBody CreatorCreateDTO creatorCreateDTO,
            HttpServletRequest httpServletRequest) {
        // System.out.println("📥 입력 데이터: " + creatorCreateDTO);
        // TODO: DB에 창작자 등록하는 로직 Service에 작성
        // creatorService.registCreator();
        String OAuthId = httpServletRequest.getUserPrincipal().getName();
        creatorService.createCreator(creatorCreateDTO, OAuthId);
        return ResponseEntity.ok("창작가가 성공적으로 저장되었습니다!");
    }

    /** 🔹 [GET] 특정 창작가 정보 불러오기 */
    /** 🔹 [GET] 특정 창작가 정보 불러오기 */
    @GetMapping("/campaign")
    public String getCreatorCampaignPage(Model model, HttpServletRequest request) {
        String OAuthId = request.getUserPrincipal().getName(); // 현재 로그인한 사용자 ID 가져오기
        CreatorDTO creator = creatorService.findCreatorByUser(OAuthId); // DB에서 창작가 정보 가져오기

        if (creator == null) {
            model.addAttribute("errorMessage", "창작가 정보를 찾을 수 없습니다.");
            return "error"; // 오류 페이지로 이동
        }

        // ✅ 이미지 서버의 기본 URL (ex: AWS S3, 서버 내 저장소 등)
        String imageBaseUrl = "/images/";

        // ✅ `imageId`를 이용해 URL 직접 생성
        model.addAttribute("creator", creator);
        model.addAttribute("profileImgUrl",
                creator.getProImgUrl() != null ? imageBaseUrl + creator.getProImgUrl().getImageId()
                        : "/images/default-profile.png");
        model.addAttribute("backImgUrl",
                creator.getBackImgUrl() != null ? imageBaseUrl + creator.getBackImgUrl().getImageId()
                        : "/images/default-background.png");

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
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> updateCreator(@RequestBody CreatorDTO creatorDTO) {
        System.out.println("📥 수정 데이터 수신: " + creatorDTO.toString());

        // 서비스에서 업데이트 로직 실행
        creatorService.updateCreator(creatorDTO);

        return ResponseEntity.ok("프로필이 성공적으로 수정되었습니다!");
    }

    /** 🔹 [GET] 캠페인 페이지 */
    @GetMapping("/campaign/{id}")
    public String getCreatorCampaignPage(@PathVariable Long id, Model model) {
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

}
