package app.scit46.ufc.service.delivery;

import org.springframework.beans.factory.annotation.Autowired;
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
import app.scit46.ufc.entity.MaterialDonationEntity;
import app.scit46.ufc.repository.MaterialDonationRepository;

import org.springframework.transaction.annotation.Transactional;
import app.scit46.ufc.entity.reward.RewardDeliveryEntity;
import app.scit46.ufc.repository.reward.RewardDeliveryRepository;

@Service
public class DeliveryService {

    private final ExecutorService executor = Executors.newFixedThreadPool(3); // 🚀 동시 요청 개수 제한
    private static final Logger logger = LoggerFactory.getLogger(DeliveryService.class);
    private static final Random random = new Random();

    @Autowired
    private MaterialDonationRepository materialDonationRepository;
    @Autowired
    private RewardDeliveryRepository rewardDeliveryRepository;

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

    public Map<String, String> trackMaterialDonations(List<MaterialDonationDTO> donations) {
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
                }, executor).thenAccept(status -> {
                    trackingResults.put(donation.getTrackingNumber(), status);

                    // ✅ 배송 상태가 "배송완료"이고 현재 상태가 "processing"이면 pending으로 변경
                    if ("배송완료".equals(status) && "processing".equals(donation.getStatus())) {
                        logger.info("✅ 배송완료 확인: 기부 ID {} → 상태 변경 (processing → pending)", donation.getDonationId());
                        updateDonationStatusToPending(donation.getDonationId());

                        // ✅ 업데이트된 상태를 다시 조회하여 반영
                        MaterialDonationEntity updatedDonation = materialDonationRepository
                                .findById(donation.getDonationId()).orElse(null);
                        if (updatedDonation != null) {
                            donation.setStatus(updatedDonation.getStatus()); // 🚀 최신 상태 반영
                        }
                    }
                })).toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return trackingResults;
    }

    @Transactional
    public void updateDonationStatusToPending(Long donationId) {
        MaterialDonationEntity donation = materialDonationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("기부 내역을 찾을 수 없습니다. ID: " + donationId));

        if (!"processing".equals(donation.getStatus())) {
            logger.warn("⚠ 상태 변경 불가: 기부 ID {}의 상태가 이미 {}입니다.", donationId, donation.getStatus());
            return;
        }

        // ✅ 상태 변경 후 저장
        donation.setStatus("pending");
        materialDonationRepository.save(donation);
        logger.info("✅ DB 업데이트 완료: 기부 ID {} → 상태 변경됨 (pending)", donationId);
    }

    public Map<String, String> trackRewardDeliveries(List<RewardDeliveryEntity> deliveries) {
        Map<String, String> trackingResults = new ConcurrentHashMap<>();

        List<CompletableFuture<Void>> futures = deliveries.stream()
                .filter(delivery -> delivery.getInvoice() != null && delivery.getInvoice().contains("#")) // 🚨 운송장 번호가
                // null이면 무시
                .map(delivery -> CompletableFuture.supplyAsync(() -> {
                    String[] parts = delivery.getInvoice().split("#");
                    String courierId = parts.length > 0 ? parts[0] : null;
                    String trackingNumber = parts.length > 1 ? parts[1] : null;

                    if (courierId != null && trackingNumber != null) {
                        return trackDelivery(courierId, trackingNumber);
                    }
                    return "미등록";
                }, executor).thenAccept(status -> {
                    trackingResults.put(delivery.getInvoice().split("#")[1], status);

                    // ✅ 배송 상태가 "배송완료"이고 현재 상태가 "shipping"이면 "completed"로 변경
                    if ("배송완료".equals(status) && "shipping".equals(delivery.getStatus())) {
                        logger.info("✅ 배송완료 확인: 배송 ID {} → 상태 변경 (shipping → completed)", delivery.getRDeliveryId());
                        updateRewardDeliveryStatusToCompleted(delivery.getRDeliveryId());

                        // ✅ 업데이트된 상태를 다시 조회하여 반영
                        RewardDeliveryEntity updatedDelivery = rewardDeliveryRepository
                                .findById(delivery.getRDeliveryId()).orElse(null);
                        if (updatedDelivery != null) {
                            delivery.setStatus(updatedDelivery.getStatus()); // 🚀 최신 상태 반영
                        }
                    }
                })).toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return trackingResults;
    }

    @Transactional
    public void updateRewardDeliveryStatusToCompleted(Long rdeliveryId) {
        RewardDeliveryEntity delivery = rewardDeliveryRepository.findById(rdeliveryId)
                .orElseThrow(() -> new IllegalArgumentException("🚨 배송 정보를 찾을 수 없음: " + rdeliveryId));

        delivery.setStatus("completed");
        rewardDeliveryRepository.save(delivery);
    }

}
