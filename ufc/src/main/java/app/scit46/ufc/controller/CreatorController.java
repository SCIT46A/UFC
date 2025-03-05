package app.scit46.ufc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("/create")
    public String create() {
        return "creator/creator-create";
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
//        creatorService.updateCreator(creator);
        return "redirect:/creator/profile/" + creator.getCreatorId();
    }

}
