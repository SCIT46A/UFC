package app.scit46.ufc.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.CreatorDTO;
import app.scit46.ufc.entity.CreatorEntity;
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

    // 검토 필요
    public void updateCreator(CreatorDTO creator) {
        creatorRepository.save(CreatorEntity.toEntity(creator,
                creator.getBusinessCert(),
                creator.getBackImgUrl(),
                creator.getProImgUrl(),
                creator.getOwnUser()));
    }

}
