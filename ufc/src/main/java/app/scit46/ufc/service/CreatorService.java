package app.scit46.ufc.service;

import app.scit46.ufc.dto.CreatorDTO;
import java.util.List;

public interface CreatorService {
    List<CreatorDTO> getPendingCreators(); // 승인 대기 목록 조회
    void approveCreator(Long creatorId); // 창작자 승인 처리
}