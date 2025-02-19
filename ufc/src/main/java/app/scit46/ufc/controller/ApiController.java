package app.scit46.ufc.controller;

import app.scit46.ufc.dto.*;
import app.scit46.ufc.exception.DBNotFoundException;
import app.scit46.ufc.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private UserAlertService userAlertService;

    @Autowired
    private CampaignService campaignService;


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

    // 검색시 사용하는 검색창
    @GetMapping("/search/{type}/{query}")
    public ResponseEntity<List<SearchResultDTO>> searchByType(
            @PathVariable("type") String type,
            @PathVariable("query") String query,
            @RequestParam(value = "sort", required = false) String sortType,
            @RequestParam(value = "donation", required = false) String donationFilter,
            @RequestParam(value = "tags", required = false) List<String> tagFilters) {

        // ✅ type이 "tag"인 경우
        if ("tag".equals(type)) {
            tagFilters = tagFilters != null ? new ArrayList<>(tagFilters) : new ArrayList<>();
            if (!query.equals("all")) {
                tagFilters.add(query); // query를 태그 필터에 추가
            }
            query = ""; // 전체 데이터를 가져오도록 query를 빈 문자열로 설정
        }


        List<SearchResultDTO> results = searchService.searchAll(query, sortType, donationFilter, tagFilters);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search/active")
    public ResponseEntity<List<SearchResultDTO>> searchActive(
            @RequestParam(value = "sort", required = false) String sortType,
            @RequestParam(value = "donation", required = false) String donationFilter,
            @RequestParam(value = "tags", required = false) List<String> tagFilters
    ) {
        List<SearchResultDTO> results = searchService.getOngoingCampaigns( sortType, donationFilter, tagFilters);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search/upcoming")
    public ResponseEntity<List<SearchResultDTO>> searchUpcoming(
            @RequestParam(value = "sort", required = false) String sortType,
            @RequestParam(value = "donation", required = false) String donationFilter,
            @RequestParam(value = "tags", required = false) List<String> tagFilters
    ) {
        List<SearchResultDTO> results = searchService.getUpcomingCampaigns(sortType, donationFilter, tagFilters);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search/product")
    public ResponseEntity<List<SearchResultDTO>> searchproduct(
            @RequestParam(value = "sort", required = false) String sortType,
            @RequestParam(value = "tags", required = false) List<String> tagFilters
    ) {
        List<SearchResultDTO> results = searchService.getSales(sortType, tagFilters);
        return ResponseEntity.ok(results);
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
