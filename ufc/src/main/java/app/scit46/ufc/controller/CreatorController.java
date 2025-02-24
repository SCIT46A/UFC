package app.scit46.ufc.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import app.scit46.ufc.dto.CreatorDTO;
import app.scit46.ufc.service.CreatorService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/creator")
@RequiredArgsConstructor
public class CreatorController {

    private final CreatorService creatorService;

    @GetMapping("/creators")
    public String getCreators(Model model) {
        List<CreatorDTO> creators = creatorService.getAllCreators();
        model.addAttribute("creators", creators);
        return "creators";

    }

    // 해당 내용 추가
    @PostMapping("/create")
    @ResponseBody
    public String createCreatorApi(@RequestBody CreatorDTO creatorDTO) {
        System.out.println("🔹 받은 데이터: " + creatorDTO.toString()); // 로그 확인
        creatorService.createCreator(creatorDTO);
        return "창작가 정보가 성공적으로 저장되었습니다.";
    }

    // 변경
    @PostMapping("/create/form")
    public String createCreatorForm(@ModelAttribute CreatorDTO creatorDTO) {
        creatorService.updateCreator(creatorDTO);
        return "redirect:/ufc/src/main/resources/templates/index.html"; // 저장 후 메인 페이지로 이동
    }

    @GetMapping("/profile")
    public String profile() {
        return "creator/creator-edit";
    }

    @GetMapping("/campaign")
    public String campaign() {
        return "creator/creator-campaign";
    }

    @GetMapping("/edit/{id}")
    public String editProfile(@PathVariable Long id, Model model) {
        CreatorDTO creator = creatorService.getCreator(id);
        model.addAttribute("creator", creator);
        return "creator/edit";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute CreatorDTO creator) {
        creatorService.updateCreator(creator);
        return "redirect:/creator/profile/" + creator.getCreatorId();
    }
}
