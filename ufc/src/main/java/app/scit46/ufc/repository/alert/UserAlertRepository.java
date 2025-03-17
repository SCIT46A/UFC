package app.scit46.ufc.repository.alert;

import app.scit46.ufc.dto.alert.UserAlertDTO;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.entity.alert.UserAlertEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface UserAlertRepository extends JpaRepository<UserAlertEntity, Long> {
    // 읽지 않은 알림 조회 (readTime이 null인 경우)
    List<UserAlertEntity> findByUserAndReadTimeIsNull(UserEntity user);

    // 읽은 알림 조회 (readTime이 null이 아닌 경우)
    List<UserAlertEntity> findByUserAndReadTimeIsNotNull(UserEntity user);

    // 특정 날짜 이후에 읽은 알림 조회
    List<UserAlertEntity> findByUserAndReadTimeAfter(UserEntity user, LocalDateTime dateTime);

    // 모든 알림을 최신순으로 조회 (읽음 상태와 관계없이)
    List<UserAlertEntity> findByUser_UserIdOrderByUserAlertIdDesc(Long userId);

}
