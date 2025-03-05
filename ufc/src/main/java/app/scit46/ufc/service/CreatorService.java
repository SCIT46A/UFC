package app.scit46.ufc.service;


import java.util.List;
import java.util.Optional;

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
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.scit46.ufc.dto.CreatorDTO;

import app.scit46.ufc.dto.custom.CreatorCreateDTO;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.repository.CreatorRepository;
import lombok.RequiredArgsConstructor;

import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.repository.CreatorRepository;
import org.springframework.web.client.RestTemplate;


@Service
@Transactional // 추가됨 아마
@RequiredArgsConstructor
public class CreatorService {
    private final CreatorRepository creatorRepository;
    private final ImageUrlService imageUrlService;
    private final UserService userService;
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

    //api 호출, 검증
    public ResponseEntity<String> callBusinessValidationAPI(CreatorApprovalDTO dto) {
        try {
            String finalUrl = "http://api.odcloud.kr/api/nts-businessman/v1/validate?serviceKey=" + apiKey;
            URI uri = new URI(finalUrl);

            Map<String, Object> requestData = new HashMap<>();
            List<Map<String, Object>> businesses = new ArrayList<>();

            Map<String, Object> businessInfo = new HashMap<>();
            businessInfo.put("b_no", dto.getBRegistNumber() != null ? dto.getBRegistNumber() : "");
            businessInfo.put("p_nm", dto.getBName() != null ? dto.getBName() : "");
            businessInfo.put("start_dt", "20231128");
            businesses.add(businessInfo);
            requestData.put("businesses", businesses);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestData, headers);


            ResponseEntity<String> response = restTemplate.postForEntity(uri, requestEntity, String.class);

            return response;

        } catch (Exception e) {
            throw new RuntimeException("🚨 사업자 검증 API 호출 실패: " + e.getMessage(), e);
        }
    }


//    ---------------------- api키-------------------------------------------

    //모든 창작자 정보
    public List<CreatorDTO> getAllCreators() {
        return creatorRepository.findAll().stream()
                .map(CreatorDTO::toDTO)
                .collect(Collectors.toList());
    }

    // 검토 필요
    //창작자 정보 업데이트 - 수정필요 - cho
//    public void updateCreator(CreatorDTO creator) {
//        creatorRepository.save(CreatorEntity.toEntity(
//                creator,
//                creator.getBusinessCert() != null ? creator.getBusinessCert().getId() : null,
//                creator.getBackImgUrl() != null ? creator.getBackImgUrl().getId() : null,
//                creator.getProImgUrl() != null ? creator.getProImgUrl().getId() : null,
//                creator.getOwnUser() != null ? creator.getOwnUser().getUserId() : null
//                UserDTO.builder().userId(creator.getOwnUser()).build()
//        ));
//    }


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


    public CreatorDTO getCreatorByUser(String oAuthId) {
        CreatorEntity creatorEntity = creatorRepository.findByOwnUser(userService.findUserByIdentity(oAuthId));

        if (creatorEntity == null) {
            return null;
        }

        return CreatorDTO.toDTO(creatorEntity);
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

    // 해당 내용 추가됨
    @Transactional
    public void createCreator(CreatorCreateDTO creatorCreateDTO, String OAuthId) {
        System.out.println("🔹 DB 저장 시작...");

        CreatorEntity creator = CreatorEntity.builder()
                .intro(creatorCreateDTO.getIntro())
                .bRegistNumber(creatorCreateDTO.getRegistNumber())
                .bName(creatorCreateDTO.getBizName())
                .companyName(creatorCreateDTO.getCompanyName())
                .address(creatorCreateDTO.getAddress())
                .creatorStatus(false) // 기본값: 미승인
                .proImgUrl(imageUrlService.findByImageId(creatorCreateDTO.getProfileImg()))
                .backImgUrl(imageUrlService.findByImageId(creatorCreateDTO.getBackImg()))
                .ownUser(userService.findUserByIdentity(OAuthId))
                .build();

        creatorRepository.save(creator);
        System.out.println("✅ DB 저장 완료!");
    }

    /** 🔹 특정 사용자(로그인한 창작자)의 정보 가져오기 */
    public CreatorDTO findCreatorByUser(String oAuthId) {
        CreatorEntity creatorEntity = creatorRepository.findByOwnUser(userService.findUserByIdentity(oAuthId));

        if (creatorEntity == null) {
            return null;
        }



    // ✅ 창작자 승인 상태를 변경하고 저장하는 메서드 추가
    public CreatorEntity saveCreator(CreatorEntity creator) {
        return creatorRepository.save(creator); // JPA를 이용한 저장
    }



    // 검토 필요
//    public void updateCreator(CreatorDTO creator) {
//        테스트하는데 문제생겨서 주석했습니다 필요 시 문의주세요 - cho
//        creatorRepository.save(CreatorEntity.toEntity(creator,
//                ImageUrlDTO.builder().id(creator.getBusinessCert()).build(),
//                ImageUrlDTO.builder().id(creator.getBackImgUrl()).build(),
//                ImageUrlDTO.builder().id(creator.getProImgUrl()).build(),
//                UserDTO.builder().userId(creator.getOwnUser()).build()));
//    }

    public CreatorEntity findByOwnUser(UserEntity user) {
        CreatorEntity creator = creatorRepository.findByOwnUser(user);
        if (creator == null) {
            throw new RuntimeException("창작자를 찾을 수 없습니다.");
        }
        return creator;
    }

        return CreatorDTO.toDTO(creatorEntity);
    }

    // 기존 getCreator 메서드 삭제 - 중복 메서드 해결
    @Transactional
    public boolean updateCreator(CreatorDTO creator) {
        if (creator == null || creator.getCreatorId() == null) {
            throw new IllegalArgumentException("❌ 잘못된 요청: CreatorDTO 또는 CreatorID가 null입니다!");
        }

        CreatorEntity creatorEntity = creatorRepository.findById(creator.getCreatorId())
                .orElseThrow(() -> new RuntimeException("❌ 창작자를 찾을 수 없습니다!"));

        // ✅ Null 체크 후 값 할당
        creatorEntity.setIntro(creator.getIntro() != null ? creator.getIntro() : creatorEntity.getIntro());
        creatorEntity.setCompanyName(
                creator.getCompanyName() != null ? creator.getCompanyName() : creatorEntity.getCompanyName());
        creatorEntity.setBName(creator.getBName() != null ? creator.getBName() : creatorEntity.getBName());

        creatorRepository.save(creatorEntity);
        System.out.println("✅ 프로필 업데이트 완료!");
        return true;
    }
}


