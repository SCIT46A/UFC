package app.scit46.ufc.entity;

import app.scit46.ufc.dto.CourierDTO;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "courier_name")
    private String courierName;

    public static CourierEntity toEntity(CourierDTO dto) {
        return CourierEntity.builder()
                .courierId(dto.getCourierId())
                .courierName(dto.getCourierName())
                .build();
    }

}
