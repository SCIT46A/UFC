package app.scit46.ufc.service;

import app.scit46.ufc.dto.CreatorDTO;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.repository.CreatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreatorServiceImpl implements CreatorService { // ✅ 인터페이스 구현
    private final CreatorRepository creatorRepository;

    public CreatorServiceImpl(CreatorRepository creatorRepository) {
        this.creatorRepository = creatorRepository;
    }

    @Override
    public List<CreatorDTO> getPendingCreators() {
        return creatorRepository.findByCreatorStatusFalseWithUser().stream()
                .map(CreatorDTO::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void approveCreator(Long creatorId) {
        CreatorEntity creator = creatorRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("창작자를 찾을 수 없습니다."));
        creator.setCreatorStatus(1);
    }
}
