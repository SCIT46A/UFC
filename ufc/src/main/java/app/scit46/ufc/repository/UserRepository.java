package app.scit46.ufc.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import app.scit46.ufc.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByOauthId(String identity);

    Optional<UserEntity> findByUserName(String userName);

    // ✅ 정지된 유저 리스트 가져오기 (userStatus = 0)
    List<UserEntity> findAllByUserStatus(int userStatus);

}
