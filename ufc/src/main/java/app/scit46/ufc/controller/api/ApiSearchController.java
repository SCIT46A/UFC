package app.scit46.ufc.controller.api;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.scit46.ufc.dto.SearchResultDTO;
import app.scit46.ufc.service.SearchService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiSearchController {

  private final SearchService searchService;
  // 검색시 사용하는 검색창
  @GetMapping("/search/{type}/{query}")
  public ResponseEntity<List<SearchResultDTO>> searchByType(
          @PathVariable("type") String type,
          @PathVariable("query") String query,
          @RequestParam(value = "sort", required = false) String sortType,
          @RequestParam(value = "donation", required = false) String donationFilter,
          @RequestParam(value = "tags", required = false) List<String> tagFilters,
          HttpServletRequest request) {
      HttpSession session = request.getSession(false);
      Long loginUserId = null;
      if (session != null) {
          loginUserId = (Long) session.getAttribute("loginUserId");
      }
      if ("tag".equals(type)) {
          tagFilters = tagFilters != null ? new ArrayList<>(tagFilters) : new ArrayList<>();
          if (!query.equals("all")) {
              tagFilters.add(query);
          }
          query = "";
      }
      List<SearchResultDTO> results = searchService.searchAll(query, sortType, donationFilter, tagFilters, loginUserId);
      return ResponseEntity.ok(results);
  }

    @GetMapping("/search/active")
    public ResponseEntity<List<SearchResultDTO>> searchActive(
            @RequestParam(value = "sort", required = false) String sortType,
            @RequestParam(value = "donation", required = false) String donationFilter,
            @RequestParam(value = "tags", required = false) List<String> tagFilters,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long loginUserId = (session != null) ? (Long) session.getAttribute("loginUserId") : null;
        List<SearchResultDTO> results = searchService.getOngoingCampaigns(sortType, donationFilter, tagFilters, loginUserId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search/upcoming")
    public ResponseEntity<List<SearchResultDTO>> searchUpcoming(
            @RequestParam(value = "sort", required = false) String sortType,
            @RequestParam(value = "donation", required = false) String donationFilter,
            @RequestParam(value = "tags", required = false) List<String> tagFilters,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long loginUserId = (session != null) ? (Long) session.getAttribute("loginUserId") : null;
        List<SearchResultDTO> results = searchService.getUpcomingCampaigns(sortType, donationFilter, tagFilters, loginUserId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search/product")
    public ResponseEntity<List<SearchResultDTO>> searchProduct(
            @RequestParam(value = "sort", required = false) String sortType,
            @RequestParam(value = "tags", required = false) List<String> tagFilters,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long loginUserId = (session != null) ? (Long) session.getAttribute("loginUserId") : null;
        List<SearchResultDTO> results = searchService.getSales(sortType, tagFilters, loginUserId);
        return ResponseEntity.ok(results);
    }
}
