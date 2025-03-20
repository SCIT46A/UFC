package app.scit46.ufc.controller.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/recommendations")
@Slf4j
public class ApiPythonController {

    @Autowired
    private RestTemplate restTemplate;  // RestTemplate Bean 등록 필요

    // Flask의 POST 엔드포인트 URL (실제 배포된 주소로 수정)
    private final String flaskUserEndpointUrl = "http://sorakaze.duckdns.org:9998/recommendations/user";
    private final String flaskUpdateEndpointUrl = "http://sorakaze.duckdns.org:9998/recommendations/update";

    // 추천 조회 (사용자 관련)
    @PostMapping("/user")
    public ResponseEntity<?> getRecommendations(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long userId = 0L;  // 기본값 0

        // 세션과 loginUserId가 존재하면 해당 값을 사용
        if (session != null && session.getAttribute("loginUserId") != null) {
            userId = (Long) session.getAttribute("loginUserId");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = Map.of("user_id", userId);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(flaskUserEndpointUrl, entity, String.class);

            // Jackson ObjectMapper를 사용하여 JSON 파싱
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode campaigns = root.path("campaigns");

            return ResponseEntity.ok(campaigns);
        } catch (Exception e) {
            log.error("Flask API 호출 중 에러 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("추천 불러오기 실패: " + e.getMessage());
        }
    }

    @PostMapping("/update")

    // 모델 업데이트 호출
    public ResponseEntity<?> updateRecommendations() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 업데이트 엔드포인트는 별도의 요청 바디 없이 호출 (필요 시 추가 정보 전달 가능)
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(flaskUpdateEndpointUrl, entity, String.class);
            log.info("Flask 업데이트 결과: {}", response.getBody());
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            log.error("Flask 업데이트 API 호출 중 에러 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("업데이트 실패: " + e.getMessage());
        }
    }
}
