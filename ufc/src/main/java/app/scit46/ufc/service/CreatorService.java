package app.scit46.ufc.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.scit46.ufc.dto.CreatorDTO;
import app.scit46.ufc.dto.custom.CreatorCreateDTO;
import app.scit46.ufc.entity.CreatorEntity;
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

    @Transactional
    public void approveCreator(Long creatorId) {
        CreatorEntity creator = creatorRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("창작자를 찾을 수 없습니다."));
        creator.setCreatorStatus(true);
    }

    // 검토 필요
    public void updateCreator(CreatorDTO creator) {
        // 테스트하는데 문제생겨서 주석했습니다 필요 시 문의주세요 - cho
        // creatorRepository.save(CreatorEntity.toEntity(creator,
        // ImageUrlDTO.builder().id(creator.getBusinessCert()).build(),
        // ImageUrlDTO.builder().id(creator.getBackImgUrl()).build(),
        // ImageUrlDTO.builder().id(creator.getProImgUrl()).build(),
        // UserDTO.builder().userId(creator.getOwnUser()).build()));
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

    // 기존 getCreator 메서드 삭제 - 중복 메서드 해결
    @Transactional
    public void updateCreatorProfile(CreatorDTO creatorDTO) {
        CreatorEntity creator = creatorRepository.findById(creatorDTO.getCreatorId())
                .orElseThrow(() -> new RuntimeException("창작자를 찾을 수 없습니다!"));

        creator.setIntro(creatorDTO.getIntro());
        creator.setCompanyName(creatorDTO.getCompanyName());
        creator.setAddress(creatorDTO.getAddress());

        creatorRepository.save(creator);
        System.out.println("✅ 프로필 업데이트 완료!");
    }
}
