
package app.scit46.ufc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.scit46.ufc.entity.UserBadgeEntity;

public interface UserBadgeRepository extends JpaRepository<UserBadgeEntity, Long> {
    List<UserBadgeEntity> findByUser_UserId(Long userId);
}
