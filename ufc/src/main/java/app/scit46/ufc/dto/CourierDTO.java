package app.scit46.ufc.dto;

import app.scit46.ufc.entity.BadgeEntity;
import app.scit46.ufc.entity.CourierEntity;
import app.scit46.ufc.entity.ImageUrlEntity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CourierDTO {


    private String courierId;
    private String courierName;



    public static CourierDTO toDTO(CourierEntity entity) {
        return CourierDTO.builder()
                .courierId(entity.getCourierId())
                .courierName(entity.getCourierName())
                .build();
    }
}
