package app.scit46.ufc.repository;

import app.scit46.ufc.entity.CourierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CourierRepository extends JpaRepository<CourierEntity, String> {

    @Query("SELECT c.courierName FROM CourierEntity c WHERE c.courierId = :courierId")
    String findCourierNameByCourierId(@Param("courierId") String courierId);
}
