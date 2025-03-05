package app.scit46.ufc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.UserEntity;

@Repository
public interface CreatorRepository extends JpaRepository<CreatorEntity, Long> {

    @Query("SELECT c FROM CreatorEntity c JOIN FETCH c.ownUser WHERE c.creatorStatus = false")
    List<CreatorEntity> findByCreatorStatusFalseWithUser();

    CreatorEntity findByOwnUser(UserEntity ownUser);
}
