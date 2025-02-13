package app.scit46.ufc.controller;

import app.scit46.ufc.dto.*;
import app.scit46.ufc.exception.DBNotFoundException;
import app.scit46.ufc.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public List<CampaignDTO> findCampaign() {
        List<CampaignDTO> campaigns = campaignService.getAllCampaigns();
        return campaigns;
    }



}
