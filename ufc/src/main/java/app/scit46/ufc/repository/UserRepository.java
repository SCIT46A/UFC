package app.scit46.ufc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.scit46.ufc.entity.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByOauthId(String identity);

    Optional<UserEntity> findByUserName(String userName);
    
}
