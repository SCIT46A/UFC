package app.scit46.ufc.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.scit46.ufc.dto.CreatorDTO;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.repository.CreatorRepository;

@Service
public class CreatorService {

    private final CreatorRepository creatorRepository;

    public CreatorService(CreatorRepository creatorRepository) {
        this.creatorRepository = creatorRepository;
    }

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
//        테스트하는데 문제생겨서 주석했습니다 필요 시 문의주세요 - cho
//        creatorRepository.save(CreatorEntity.toEntity(creator,
//                ImageUrlDTO.builder().id(creator.getBusinessCert()).build(),
//                ImageUrlDTO.builder().id(creator.getBackImgUrl()).build(),
//                ImageUrlDTO.builder().id(creator.getProImgUrl()).build(),
//                UserDTO.builder().userId(creator.getOwnUser()).build()));
    }

    public CreatorEntity findByOwnUser(UserEntity user) {
        CreatorEntity creator = creatorRepository.findByOwnUser(user);
        if (creator == null) {
            throw new RuntimeException("창작자를 찾을 수 없습니다.");
        }
        return creator;
    }
}
