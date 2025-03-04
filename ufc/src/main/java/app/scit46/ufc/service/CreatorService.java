package app.scit46.ufc.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.scit46.ufc.dto.CreatorDTO;
import app.scit46.ufc.dto.custom.CreatorCreateDTO;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.repository.CreatorRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional // 추가됨 아마
@RequiredArgsConstructor
public class CreatorService {

    private final CreatorRepository creatorRepository;
    private final ImageUrlService imageUrlService;
    private final UserService userService;

    public List<CreatorDTO> getAllCreators() {
        return creatorRepository.findAll().stream()
                .map(CreatorDTO::toDTO)
                .collect(Collectors.toList());
    }

    // 검토 필요
    public CreatorDTO getCreator(Long id) {
        return CreatorDTO.toDTO(creatorRepository.findById(id).orElse(null));
    }

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

    @Transactional
    public void approveCreator(Long creatorId) {
        CreatorEntity creator = creatorRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("창작자를 찾을 수 없습니다."));
        creator.setCreatorStatus(true);
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

    /** 🔹 현재 로그인한 사용자의 창작가 정보 가져오기 */
    public CreatorDTO findCreatorByUser(String oAuthId) {
        UserEntity user = userService.findUserByIdentity(oAuthId);

        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // ✅ 새로운 쿼리 메서드 사용 (Optional 처리)
        CreatorEntity creatorEntity = creatorRepository.findCreatorByUser(user)
                .orElseThrow(() -> new RuntimeException("창작가 정보를 찾을 수 없습니다."));

        return CreatorDTO.toDTO(creatorEntity);
    }

    // 기존 getCreator 메서드 삭제 - 중복 메서드 해결
    @Transactional
    public void updateCreator(CreatorDTO creator) {
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
    }
}
