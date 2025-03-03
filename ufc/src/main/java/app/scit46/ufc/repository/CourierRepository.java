package app.scit46.ufc.repository;

import app.scit46.ufc.entity.CourierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierRepository extends JpaRepository<CourierEntity, String> {
}
