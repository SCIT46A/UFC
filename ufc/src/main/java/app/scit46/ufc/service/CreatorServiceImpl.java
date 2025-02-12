package app.scit46.ufc.service;

import app.scit46.ufc.dto.CreatorDTO;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.repository.CreatorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreatorServiceImpl implements CreatorService {
    private final CreatorRepository creatorRepository;

    public CreatorServiceImpl(CreatorRepository creatorRepository) {
        this.creatorRepository = creatorRepository;
    }

    // ✅ 승인 대기 중인 창작자 조회 (creator_status = false)
    @Override
    public List<CreatorDTO> getPendingCreators() {
        return creatorRepository.findByCreatorStatusFalse().stream()
                .map(CreatorDTO::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ 창작자 승인 처리 (creator_status = true)
    @Override
    public void approveCreator(Long creatorId) {
        CreatorEntity creator = creatorRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("창작자를 찾을 수 없습니다."));
        creator.setCreatorStatus(true); // ✅ Boolean 사용 (true = 승인)
        creatorRepository.save(creator);
    }
}
