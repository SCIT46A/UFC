package app.scit46.ufc.dto;

import app.scit46.ufc.entity.CourierEntity;
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
