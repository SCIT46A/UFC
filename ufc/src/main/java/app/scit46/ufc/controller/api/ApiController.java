package app.scit46.ufc.controller.api;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.scit46.ufc.dto.SearchDTO;
import app.scit46.ufc.dto.SearchResultDTO;
import app.scit46.ufc.dto.TagDTO;
import app.scit46.ufc.dto.UserAlertDTO;
import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.exception.DBNotFoundException;
import app.scit46.ufc.service.SearchService;
import app.scit46.ufc.service.UserAlertService;
import app.scit46.ufc.service.UserService;
import app.scit46.ufc.service.tag.TagService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ApiController {
    
    private final UserService userService;

    private final TagService tagService;
    
    private final SearchService searchService;
    
    private final UserAlertService userAlertService;


    

    //  카테고리 입력
    @GetMapping("/checkTag")
    public List<TagDTO> checkTag() {
        return tagService.getTopTags();
    }

    @GetMapping("/searchBox")
    public List<SearchDTO> search(@RequestParam("keyword") String keyword) {
        log.info(searchService.search_target(keyword).toString());
        return searchService.search_target(keyword);
    }

    @GetMapping("/searchTagBox")
    public List<TagDTO> searchTag(@RequestParam("keyword") String keyword) {
        log.info(searchService.search_target(keyword).toString());
        return searchService.searchTagTarget(keyword);
    }

    //  알림상태 확인하는거
    @GetMapping("/checkAlert")
    public List<UserAlertDTO> checkAlert(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 세션 가져오기

        if (session != null) {
            Long loginUserId = (Long) session.getAttribute("loginUserId"); // 세션에서 userId 가져오기
            return userAlertService.alertCheck(loginUserId);
        }

        return Collections.emptyList(); // 세션이 없거나, 알람이 없음
    }

    @GetMapping("/checkLogin")
    public UserDTO checkLogin(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 세션 가져오기
        UserDTO user = null;
        if (session != null) {
            Long loginUserId = (Long) session.getAttribute("loginUserId"); // 세션에서 userId 가져오기
            try {
                user = userService.readUserById(loginUserId);
                return user;
            } catch (DBNotFoundException e) {
                return null;
            }
        }
        return user;
    }

    

    @GetMapping("/lowertDonation")
    public ResponseEntity<List<SearchResultDTO>> lowertDonation() {
        List<SearchResultDTO> results = searchService.findLowestDonationRateCampaigns();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/likeTopCampaign")
    public ResponseEntity<List<SearchResultDTO>> findTop10CampaignsByLikes() {
        List<SearchResultDTO> results = searchService.findTop10CampaignsByLikes();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/likeTopProduct")
    public ResponseEntity<List<SearchResultDTO>> findTop10ProductsByLikes() {
        List<SearchResultDTO> results = searchService.findTop10ProductsByLikes();
        return ResponseEntity.ok(results);
    }

}
