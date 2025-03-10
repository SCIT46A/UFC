package app.scit46.ufc.service;

import java.util.List;
import java.util.Optional;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;

import app.scit46.ufc.dto.CreatorApprovalDTO;
import app.scit46.ufc.dto.ImageUrlDTO;
import app.scit46.ufc.dto.UserDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.scit46.ufc.dto.CreatorDTO;

import app.scit46.ufc.dto.custom.CreatorCreateDTO;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.ImageUrlEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.repository.CreatorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.repository.CreatorRepository;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional // 추가됨 아마
@RequiredArgsConstructor
@Slf4j
public class CreatorService {
    private final CreatorRepository creatorRepository;
    private final ImageUrlService imageUrlService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private static final RestTemplate restTemplate = new RestTemplate();

    @Value("${opendata.enc-key}")
    private String apiKey;

    private String apiUrl;

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

    // api 호출, 검증
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

    // ---------------------- api키-------------------------------------------

    // 모든 창작자 정보
    public List<CreatorDTO> getAllCreators() {
        return creatorRepository.findAll().stream()
                .map(CreatorDTO::toDTO)
                .collect(Collectors.toList());
    }

    // 검토 필요

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

        // 값 확인
        System.out.println("Service에서 받은 데이터: " + creatorCreateDTO);

        // ✅ bRegistDate가 null이면 기본값 설정 (기본값으로 오늘 날짜가 들어가고 있었음)
        if (creatorCreateDTO.getBRegistDate() == null) {
            System.out.println("⚠️ bRegistDate가 null이므로 기본값(오늘 날짜) 설정");
        } else {
            System.out.println("📅 Service에서 받은 bRegistDate: " + creatorCreateDTO.getBRegistDate());
        }

        CreatorEntity creator = CreatorEntity.builder()
                .intro(creatorCreateDTO.getIntro())
                .bRegistNumber(creatorCreateDTO.getRegistNumber())
                .bName(creatorCreateDTO.getBizName())
                .companyName(creatorCreateDTO.getCompanyName())
                .address(creatorCreateDTO.getAddress())
                .bRegistDate(creatorCreateDTO.getBRegistDate()) // 추가된 필드
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
        // 임의로 추가해두었습니다 필요시 수정해주세요~ -cho
        return CreatorDTO.toDTO(creatorEntity);
    }

    // 하단부분에 사용하는거 없어서 일단 주석해두었습니다 궁금하신점은 문의주세요~ -cho

    // ✅ 창작자 승인 상태를 변경하고 저장하는 메서드 추가
    public CreatorEntity saveCreator(CreatorEntity creator) {
        return creatorRepository.save(creator); // JPA를 이용한 저장
    }

    public CreatorEntity findByOwnUser(UserEntity user) {
        CreatorEntity creator = creatorRepository.findByOwnUser(user);
        if (creator == null) {
            throw new RuntimeException("창작자를 찾을 수 없습니다.");
        }
        return creator;
    }

    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public CreatorEntity findCreatorById(Long creatorId) {
        return creatorRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 ID: " + creatorId));
    }

    // 창작가 정보 업데이트
    // public boolean updateCreator(CreatorCreateDTO creatorDTO) {
    // try {
    // // ✅ 기존 Creator 조회 (ID 필요)
    // CreatorEntity creator = creatorRepository.findById(creatorDTO.getId())
    // .orElseThrow(() -> new IllegalArgumentException("❌ 창작가를 찾을 수 없습니다!"));

    // // ✅ 변경된 값만 업데이트 (null이면 기존 값 유지)
    // if (creatorDTO.getIntro() != null) {
    // creator.setIntro(creatorDTO.getIntro());
    // }
    // if (creatorDTO.getBizName() != null) {
    // creator.setBName(creatorDTO.getBizName());
    // }
    // if (creatorDTO.getCompanyName() != null) {
    // log.info("회사명 수정은 불가능합니다."); // ✅ 회사명은 수정 불가
    // }
    // if (creatorDTO.getBRegistDate() != null) {
    // creator.setBRegistDate(creatorDTO.getBRegistDate());
    // }

    // // ✅ 프로필 이미지 업데이트 (새로운 이미지가 있을 경우만 변경)
    // if (creatorDTO.getProfileImg() != null &&
    // creatorDTO.getProfileImg().getImageId() != null) {
    // ImageUrlEntity profileImage =
    // imageUrlService.findByImageId(creatorDTO.getProfileImg().getImageId());
    // if (profileImage != null) {
    // creator.setProImgUrl(ImageUrlDTO.toDTO(profileImage));
    // }
    // }

    // // ✅ 배경 이미지 업데이트 (새로운 이미지가 있을 경우만 변경)
    // if (creatorDTO.getBackImg() != null && creatorDTO.getBackImg().getImageId()
    // != null) {
    // ImageUrlEntity backImage =
    // imageUrlService.findByImageId(creatorDTO.getBackImg().getImageId());
    // if (backImage != null) {
    // creator.setBackImgUrl(ImageUrlDTO.toDTO(backImage));
    // }
    // }

    // log.info("📌 DB에 수정된 유저 정보 저장 중...");
    // creatorRepository.save(creator);
    // log.info("✅ 업데이트 완료!");

    // return true;
    // } catch (Exception e) {
    // log.error("❌ 업데이트 중 오류 발생: {}", e.getMessage());
    // e.printStackTrace();
    // return false;
    // }
    // }

    public boolean updateCreator(CreatorDTO creator, CreatorCreateDTO creatorDTO) {
        try {
            // ✅ 변경된 값만 업데이트 (null이면 기존 값 유지)
            if (creatorDTO.getIntro() != null) {
                creator.setIntro(creatorDTO.getIntro());
            }
            if (creatorDTO.getBizName() != null) {
                creator.setBName(creatorDTO.getBizName());
            }
            if (creatorDTO.getCompanyName() != null) {
                // ✅ 사업자 회사명은 수정 불가 (기존 코드 유지)
                log.info("회사명 수정은 불가능합니다.");
            }
            if (creatorDTO.getBRegistDate() != null) {
                creator.setBRegistDate(creatorDTO.getBRegistDate());
            }

            // ✅ 프로필 이미지 업데이트 (새로운 이미지가 있을 경우만 변경)
            if (creatorDTO.getProfileImg() != null) {
                ImageUrlDTO image = ImageUrlDTO.toDTO(imageUrlService.findByImageId(creatorDTO.getProfileImg()));
                if (image != null && image.getId() != null) {
                    creator.setProImgUrl(image);
                }
            }

            // ✅ 배경 이미지 업데이트 (새로운 이미지가 있을 경우만 변경)
            if (creatorDTO.getBackImg() != null) {
                ImageUrlDTO image = ImageUrlDTO.toDTO(imageUrlService.findByImageId(creatorDTO.getBackImg()));
                if (image != null && image.getId() != null) {
                    creator.setBackImgUrl(image);
                }
            }

            log.info("📌 DB에 수정된 유저 정보 저장 중...");
            creatorRepository.save(CreatorEntity.toEntity(creator)); // ✅ `CreatorEntity`
            log.info("✅ 업데이트 완료!");

            return true;
        } catch (Exception e) {
            log.error("❌ 업데이트 중 오류 발생: {}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
