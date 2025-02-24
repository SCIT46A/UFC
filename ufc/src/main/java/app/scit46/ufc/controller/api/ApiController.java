package app.scit46.ufc.controller.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import app.scit46.ufc.dto.custom.CampaignWithGoalsDTO;
import app.scit46.ufc.dto.custom.IntroPageCampaignDTO;
import app.scit46.ufc.service.LikeService;
import app.scit46.ufc.service.campaign.CampaignService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    private final LikeService likeService;
    private final CampaignService campaignService;


    //  카테고리 입력
    @GetMapping("/checkTag")
    public List<TagDTO> checkTag() {
        return tagService.getTopTags();
    }

    @GetMapping("/searchBox")
    public List<SearchDTO> search(@RequestParam("keyword") String keyword) {
        return searchService.search_target(keyword);
    }

    @GetMapping("/searchTagBox")
    public List<TagDTO> searchTag(@RequestParam("keyword") String keyword) {
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
    public ResponseEntity<List<CampaignWithGoalsDTO>> getCampaignsWithGoals() {
        List<CampaignWithGoalsDTO> list = searchService.getOngoingCampaignsWithGoals();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/likeTopCampaign")
    public ResponseEntity<List<SearchResultDTO>> findTop10CampaignsByLikes(
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long loginUserId = (session != null) ? (Long) session.getAttribute("loginUserId") : null;
        List<SearchResultDTO> results = searchService.findTop10CampaignsByLikes(loginUserId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/likeTopProduct")
    public ResponseEntity<List<SearchResultDTO>> findTop10ProductsByLikes(
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long loginUserId = (session != null) ? (Long) session.getAttribute("loginUserId") : null;
        List<SearchResultDTO> results = searchService.findTop10ProductsByLikes(loginUserId);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/like/toggle")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @RequestParam("itemId") Long itemId,
            @RequestParam("type") String type,
            @RequestParam("currentState") boolean currentState,
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        Long loginUserId = (session != null) ? (Long) session.getAttribute("loginUserId") : null;
        if (loginUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        boolean newState = likeService.toggleLike(itemId, type, currentState, loginUserId);
        String message = newState ? "좋아요가 추가되었습니다." : "좋아요가 취소되었습니다.";
        return ResponseEntity.ok(Map.of("success", true, "isLiked", newState, "message", message));
    }

}
