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
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.Random;

import app.scit46.ufc.dto.MaterialDonationDTO;

@Service
public class DeliveryService {

    private final ExecutorService executor = Executors.newFixedThreadPool(3); // 🚀 동시 요청 개수 제한
    private static final Logger logger = LoggerFactory.getLogger(DeliveryService.class);
    private static final Random random = new Random();

    @Value("${tracker.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public DeliveryService() {
        this.restTemplate = new RestTemplate();
    }

    public String trackDelivery(String courierId, String trackingNumber) {
        if (courierId == null || trackingNumber == null) {
            return "잘못된 운송장 정보";
        }

        String url = "https://apis.tracker.delivery/graphql";

        try {
            // 🚀 랜덤 딜레이 추가 (API 과부하 방지)
            Thread.sleep(85 + random.nextInt(200));

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode requestBody = objectMapper.createObjectNode();

            String graphqlQuery = """
                    query Track($carrierId: ID!, $trackingNumber: String!) {
                        track(carrierId: $carrierId, trackingNumber: $trackingNumber) {
                            lastEvent {
                                time
                                status { code name }
                                description
                            }
                        }
                    }
                    """;

            requestBody.put("query", graphqlQuery);

            ObjectNode variables = objectMapper.createObjectNode();
            variables.put("carrierId", courierId);
            variables.put("trackingNumber", trackingNumber);
            requestBody.set("variables", variables);

            String requestBodyString = objectMapper.writeValueAsString(requestBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "TRACKQL-API-KEY " + apiKey);

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyString, headers);

            logger.info("🚀 배송조회 API 요청: {}", trackingNumber);

            String response = restTemplate.postForObject(url, requestEntity, String.class);
            logger.info("🚚 응답 데이터: {}", response);

            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.has("errors")) {
                return "배송 조회 실패";
            }

            JsonNode lastEvent = rootNode.path("data").path("track").path("lastEvent");

            if (lastEvent.isMissingNode()) {
                return "운송장 정보를 찾을 수 없습니다.";
            }

            return lastEvent.path("status").path("name").asText();

        } catch (Exception e) {
            logger.error("🚨 배송 조회 중 오류 발생:", e);
            return "배송 정보를 가져올 수 없습니다.";
        }
    }

    public Map<String, String> trackMultipleDeliveries(List<MaterialDonationDTO> donations) {
        Map<String, String> trackingResults = new ConcurrentHashMap<>();

        List<CompletableFuture<Void>> futures = donations.stream()
                .filter(donation -> donation.getTrackingNumber() != null) // 🚨 운송장 번호가 null이면 무시
                .map(donation -> CompletableFuture.supplyAsync(() -> {
                    String courierId = donation.getCourierId();
                    String trackingNumber = donation.getTrackingNumber();

                    if (courierId != null) {
                        return trackDelivery(courierId, trackingNumber);
                    }
                    return "미등록";
                }, executor).thenAccept(status -> trackingResults.put(donation.getTrackingNumber(), status)))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return trackingResults;
    }
}
