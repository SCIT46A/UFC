package app.scit46.ufc.dto;

import app.scit46.ufc.entity.UserAlertEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserAlertDTO {
    private Long userAlertId;
    private Long userId;
    private Boolean isRead;

    public static UserAlertDTO toDTO(UserAlertEntity entity) {
        return UserAlertDTO.builder()
                .userAlertId(entity.getUserAlertId())
                .userId(entity.getUser() != null ? entity.getUser().getUserId() : null)
                .isRead(entity.getIsRead())
                .build();
    }
}
