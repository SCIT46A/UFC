package app.scit46.ufc.repository;

import app.scit46.ufc.entity.CreatorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreatorRepository extends JpaRepository<CreatorEntity, Long> {

    // ✅ 승인 대기 중인 창작자 목록 (creator_status = false)
    List<CreatorEntity> findByCreatorStatusFalse();
}
