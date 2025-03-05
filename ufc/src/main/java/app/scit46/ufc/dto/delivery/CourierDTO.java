package app.scit46.ufc.dto.delivery;

import app.scit46.ufc.entity.CourierEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Builder;

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
