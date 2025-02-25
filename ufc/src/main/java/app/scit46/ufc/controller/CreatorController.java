package app.scit46.ufc.controller;

import java.net.http.HttpRequest;

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
import app.scit46.ufc.service.CreatorService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/creator") // 🔹 모든 URL이 "/creator"로 시작하도록 설정
@RequiredArgsConstructor
public class CreatorController {

    private final CreatorService creatorService;

    /** 🔹 [GET] 창작가 개설 페이지 출력 */
    /*
     * @GetMapping("/create")
     * public String getCreatorPage(Model model, HttpServletRequest request) {
     * model.addAttribute("message", "창작가 개설 페이지입니다!");
     * model.addAttribute("username", request.getUserPrincipal().getName().trim());
     * return "creator/creator-create"; // ✅ ".html" 붙이지 않음!
     * }
     */

    /** 🔹 [GET] 창작가 개설 페이지 출력 */
    @GetMapping("/create")
    public String getCreatorPage(Model model) {
        model.addAttribute("message", "창작가 개설 페이지입니다!");
        return "creator/creator-create"; // ✅ ".html" 붙이지 않음!
    }

    /** 🔹 [GET] 창작가 캠페인 페이지 출력 */
    @GetMapping("/creator-campaign")
    public String getCreatorCampaignPage(Model model) {
        model.addAttribute("message", "창작가 캠페인 페이지입니다!");
        return "creator/creator-campaign"; // ✅ ".html" 붙이지 않음!
    }

    /** 🔹 [GET] 창작가 프로필 수정 페이지 출력 */
    @GetMapping("/creator-edit")
    public String getCreatorEditPage(Model model) {
        model.addAttribute("message", "창작가 프로필 수정 페이지입니다!");
        return "creator/creator-edit"; // ✅ ".html" 붙이지 않음!
    }

    /** 🔹 [POST] 입력값을 DB에 저장 */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<String> createCreator(@RequestBody CreatorDTO creatorDTO) {
        System.out.println("📥 입력 데이터: " + creatorDTO.toString());

        creatorService.createCreator(creatorDTO);
        return ResponseEntity.ok("창작가가 성공적으로 저장되었습니다!");
    }

    /** 🔹 [GET] 창작가 수정 페이지 */
    @GetMapping("/edit/{id}")
    public String editProfile(@PathVariable Long id, Model model) {
        CreatorDTO creator = creatorService.getCreator(id);
        model.addAttribute("creator", creator);
        return "creator/creator-edit";
    }

    /** 🔹 [POST] 프로필 수정 */
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> updateProfile(@RequestBody CreatorDTO creatorDTO) {
        System.out.println("📥 수정 데이터: " + creatorDTO.toString());

        creatorService.updateCreator(creatorDTO);
        return ResponseEntity.ok("프로필이 성공적으로 수정되었습니다!");
    }

    /** 🔹 [GET] 캠페인 페이지 */
    @GetMapping("/campaign/{id}")
    public String getCreatorCampaignPage(@PathVariable Long id, Model model) {
        CreatorDTO creator = creatorService.getCreator(id);
        model.addAttribute("creator", creator);
        return "creator/creator-campaign";
    }
}
