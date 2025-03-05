package app.scit46.ufc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.scit46.ufc.entity.BadgeEntity;

public interface BadgeRepository extends JpaRepository<BadgeEntity, Long> {
    List<BadgeEntity> findAll();
}
