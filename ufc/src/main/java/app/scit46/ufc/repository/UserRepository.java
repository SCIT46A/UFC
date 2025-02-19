package app.scit46.ufc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.scit46.ufc.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByOauthId(String identity);

    Optional<UserEntity> findByUserName(String userName);
    
    // ✅ 유저 정지 여부 확인 및 업데이트를 위한 메서드 추가
    Optional<UserEntity> findByUserId(Long userId);

    List<UserEntity> findAllByUserStatus(int userStatus);

}
