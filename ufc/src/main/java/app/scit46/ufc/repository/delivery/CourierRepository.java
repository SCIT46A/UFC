package app.scit46.ufc.repository.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.scit46.ufc.entity.delivery.CourierEntity;

public interface CourierRepository extends JpaRepository<CourierEntity, String> {

    @Query("SELECT c.courierName FROM CourierEntity c WHERE c.courierId = :courierId")
    String findCourierNameByCourierId(@Param("courierId") String courierId);
}
