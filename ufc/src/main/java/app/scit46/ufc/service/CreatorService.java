package app.scit46.ufc.service;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import app.scit46.ufc.dto.CreatorApprovalDTO;
import app.scit46.ufc.dto.ImageUrlDTO;
import app.scit46.ufc.dto.UserDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.scit46.ufc.dto.CreatorDTO;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.repository.CreatorRepository;
import org.springframework.web.client.RestTemplate;


@Service
public class CreatorService {
    private final CreatorRepository creatorRepository;
    private final ObjectMapper objectMapper;
    private static final RestTemplate restTemplate = new RestTemplate();

    @Value("${opendata.enc-key}")
    private String apiKey;

    private String apiUrl;

    public CreatorService(CreatorRepository creatorRepository, ObjectMapper objectMapper) {
        this.creatorRepository = creatorRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void setupApiUrl() {
        try {
            System.out.println("🔧 API URL 설정 시작...");

            this.apiUrl = "http://api.odcloud.kr/api/nts-businessman/v1/validate?serviceKey=" + apiKey;

            System.out.println("🛠 원본 API Key: " + apiKey);
            System.out.println("🚀 최종 API 호출 URL: " + this.apiUrl);

        } catch (Exception e) {
            System.err.println("🚨 API URL 설정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //모든 창작자 정보
    public List<CreatorDTO> getAllCreators() {
        return creatorRepository.findAll().stream()
                .map(CreatorDTO::toDTO)
                .collect(Collectors.toList());
    }

    // 검토 필요
    //창작자 정보 업데이트
    public void updateCreator(CreatorDTO creator) {
        creatorRepository.save(CreatorEntity.toEntity(
                creator,
                creator.getBusinessCert() != null ? ImageUrlDTO.builder().id(creator.getBusinessCert()).build() : null,
                creator.getBackImgUrl() != null ? ImageUrlDTO.builder().id(creator.getBackImgUrl()).build() : null,
                creator.getProImgUrl() != null ? ImageUrlDTO.builder().id(creator.getProImgUrl()).build() : null,
                UserDTO.builder().userId(creator.getOwnUser()).build()
        ));
    }


    // 특정 창작자 정보 조회
    public CreatorDTO getCreator(Long id) {
        return creatorRepository.findById(id)
                .map(CreatorDTO::toDTO)
                .orElseThrow(() -> new RuntimeException("창작자를 찾을 수 없습니다."));
    }

    // 특정 창작자 정보 조회 (Entity 반환)
    public CreatorEntity getCreatorById(Long id) {
        return creatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("창작자를 찾을 수 없습니다."));
    }

    // 승인 대기 창작자 조회
    public List<CreatorDTO> getPendingCreators() {
        return creatorRepository.findByCreatorStatusFalseWithUser().stream()
                .map(CreatorDTO::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ 창작자 승인 처리
    @Transactional
    public void approveCreator(Long creatorId) {
        CreatorEntity creator = getCreatorById(creatorId);

        if (creator.getBRegistNumber() == null || creator.getBRegistNumber().isEmpty()) {
            throw new RuntimeException("🚨 승인 실패: 사업자 등록번호가 없습니다. (creatorId=" + creatorId + ")");
        }

        creator.setCreatorStatus(true);
        creatorRepository.save(creator);
    }

    // ✅ 사업자 등록번호 검증 API 호출
    // ✅ 사업자 등록번호 검증 API 호출
    public void verifyCreatorBusiness(CreatorApprovalDTO dto) {
        try {
            System.out.println("✅ 사업자 등록번호 검증 시작...");

            if (dto == null) {
                throw new IllegalArgumentException("🚨 CreatorApprovalDTO가 null입니다.");
            }

            // ✅ 요청 데이터 생성
            Map<String, Object> requestData = new HashMap<>();
            List<Map<String, Object>> businesses = new ArrayList<>();

            Map<String, Object> businessInfo = new HashMap<>();
            businessInfo.put("b_no", dto.getBRegistNumber() != null ? dto.getBRegistNumber() : "");
            businessInfo.put("p_nm", dto.getBName() != null ? dto.getBName() : "");
            businessInfo.put("start_dt", "20231128");
            businessInfo.put("p_nm2", "");
            businessInfo.put("b_nm", "");
            businessInfo.put("corp_no", "");
            businessInfo.put("b_sector", "");
            businessInfo.put("b_type", "");
            businessInfo.put("b_adr", "");

            businesses.add(businessInfo);
            requestData.put("businesses", businesses);

            // ✅ HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            headers.set("Connection", "keep-alive");

            // ✅ HTTP 요청 엔티티 생성
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestData, headers);

            // ✅ 최종 URL 확인 (API Key가 URL에 포함됨)
            String finalUrl = apiUrl;
            URI uri = new URI(finalUrl);
            System.out.println("📌 [최종 요청 URL]: " + finalUrl);

            // ✅ 최종 요청 바디 확인
            String requestBody = new ObjectMapper().writeValueAsString(requestData);
            System.out.println("📌 [최종 요청 바디]: " + requestBody);

            // ✅ API 호출 (POST)
            ResponseEntity<String> response = restTemplate.postForEntity(uri, requestEntity, String.class);

            // ✅ 응답 데이터 로그 확인
            System.out.println("✅ API 응답 상태 코드: " + response.getStatusCode());
            System.out.println("✅ API 응답 본문: " + response.getBody());

        } catch (IllegalArgumentException e) {
            System.err.println("🚨 잘못된 입력 값: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("🚨 사업자 검증 API 호출 실패: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("🚨 사업자 등록번호 검증 중 오류 발생: " + e.getMessage(), e);
        }

        }
    }

