package app.scit46.ufc.service.delivery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class DeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryService.class);

    @Value("${tracker.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public DeliveryService() {
        this.restTemplate = new RestTemplate();
    }

    public String trackDelivery(String courierName, String trackingNumber) {
        String courierCode = getCourierCode(courierName);
        if (courierCode == null) {
            return "{\"error\": \"지원하지 않는 택배사입니다.\"}";
        }

        String url = "https://apis.tracker.delivery/graphql";

        try {
            // ✅ JSON 요청 바디 생성 (ObjectMapper 사용)
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode requestBody = objectMapper.createObjectNode();

            // ✅ GraphQL Query를 JSON에서 올바르게 인식하도록 문자열로 이스케이프 처리
            String graphqlQuery = """
                    query Track($carrierId: ID!, $trackingNumber: String!) {
                        track(carrierId: $carrierId, trackingNumber: $trackingNumber) {
                            lastEvent {
                                time
                                status { code name }
                                description
                            }
                            events(last: 10) {
                                edges {
                                    node {
                                        time
                                        status { code name }
                                        description
                                    }
                                }
                            }
                        }
                    }
                    """;

            requestBody.put("query", graphqlQuery);

            // ✅ "variables" JSON 객체 추가
            ObjectNode variables = objectMapper.createObjectNode();
            variables.put("carrierId", courierCode);
            variables.put("trackingNumber", trackingNumber);
            requestBody.set("variables", variables);

            String requestBodyString = objectMapper.writeValueAsString(requestBody);

            // ✅ HTTP 요청 생성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "TRACKQL-API-KEY " + apiKey);

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyString, headers);

            // 🚀 요청 로그 찍기
            logger.info("🚀 배송조회 API 요청 URL: {}", url);
            logger.info("📦 배송조회 API 요청 바디: {}", requestBodyString);
            logger.info("🔑 배송조회 API 요청 헤더: {}", headers);

            // ✅ API 요청 실행
            String response = restTemplate.postForObject(url, requestEntity, String.class);

            // 🚚 응답 로그 찍기
            logger.info("🚚 배송조회 API 응답 데이터: {}", response);

            return response;
        } catch (Exception e) {
            logger.error("🚨 배송 조회 중 오류 발생:", e);
            return "{\"error\": \"배송 정보를 가져올 수 없습니다.\"}";
        }
    }

    private String getCourierCode(String courierName) {
        return switch (courierName) {
            case "CJ대한통운" -> "kr.cjlogistics";
            case "한진택배" -> "kr.hanjin";
            case "우체국택배" -> "kr.epost";
            default -> null;
        };
    }
}
