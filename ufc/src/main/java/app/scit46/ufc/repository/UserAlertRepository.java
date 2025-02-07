package app.scit46.ufc.repository;

import app.scit46.ufc.entity.UserAlertEntity;
import app.scit46.ufc.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAlertRepository extends JpaRepository<UserAlertEntity, Long> {
    // UserEntity를 직접 사용하여 userId와 isRead를 기준으로 검색
    boolean existsByUserAndIsRead(UserEntity user, Boolean isRead);
}
