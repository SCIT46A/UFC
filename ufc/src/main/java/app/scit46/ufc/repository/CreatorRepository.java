package app.scit46.ufc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.UserEntity;

public interface CreatorRepository extends JpaRepository<CreatorEntity, Long> {

    CreatorEntity findByOwnUser(UserEntity ownUser);
    
}
