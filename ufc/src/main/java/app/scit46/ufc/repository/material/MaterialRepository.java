package app.scit46.ufc.repository.material;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import app.scit46.ufc.entity.MaterialEntity;

public interface MaterialRepository extends JpaRepository<MaterialEntity, Long> {
    Optional<MaterialEntity> findByName(String name);
}
