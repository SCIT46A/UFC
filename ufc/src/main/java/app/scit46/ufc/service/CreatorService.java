package app.scit46.ufc.service;

import app.scit46.ufc.dto.CreatorDTO;
import java.util.List;

public interface CreatorService {  // ✅ 클래스 → 인터페이스로 변경
    List<CreatorDTO> getPendingCreators();
    void approveCreator(Long creatorId);
}
