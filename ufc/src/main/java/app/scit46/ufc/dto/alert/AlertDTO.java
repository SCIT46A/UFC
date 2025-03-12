package app.scit46.ufc.dto.alert;

import lombok.*;
import java.time.LocalDateTime;

import app.scit46.ufc.entity.alert.AlertEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AlertDTO {
    private Long alertId;
    private String content;
    private String alertType;
    private String imageUrl;
    private String linkUrl;
    private LocalDateTime alertDate;

    public static AlertDTO toDTO(AlertEntity entity) {
        return AlertDTO.builder()
                .alertId(entity.getAlertId())
                .content(entity.getContent())
                .alertType(entity.getAlertType())
                .imageUrl(entity.getImageUrl())
                .linkUrl(entity.getLinkUrl())
                .alertDate(entity.getAlertDate())
                .build();
    }
}
