package app.scit46.ufc.entity.delivery;

import app.scit46.ufc.dto.delivery.CourierDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Couriers")
public class CourierEntity {
    @Id
    @Column(name = "courier_id")
    private String courierId;

    @Column(name = "courier_name", nullable = false)
    private String courierName;

    public static CourierEntity toEntity(CourierDTO dto) {
        return CourierEntity.builder()
                .courierId(dto.getCourierId())
                .courierName(dto.getCourierName())
                .build();
    }

}