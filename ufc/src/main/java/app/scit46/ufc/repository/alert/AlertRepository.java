package app.scit46.ufc.repository.alert;

import org.springframework.data.jpa.repository.JpaRepository;

import app.scit46.ufc.entity.alert.AlertEntity;

public interface AlertRepository extends JpaRepository<AlertEntity, Long> {
    
}
