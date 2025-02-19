package app.scit46.ufc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.UserEntity;

@Repository
public interface CreatorRepository extends JpaRepository<CreatorEntity, Long> {

    // ✅ 승인 대기 중인 창작자 목록 (creator_status = false)
    List<CreatorEntity> findByCreatorStatusFalse();

    @Query("SELECT c FROM CreatorEntity c JOIN FETCH c.ownUser WHERE c.creatorStatus = 0")
    List<CreatorEntity> findByCreatorStatusFalseWithUser();

    CreatorEntity findByOwnUser(UserEntity ownUser);
}
