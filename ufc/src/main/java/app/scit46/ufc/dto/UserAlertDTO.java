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
    private UserDTO user; // ✅ UserDTO 포함
    private Boolean isRead;

    public static UserAlertDTO toDTO(UserAlertEntity entity) {
        return UserAlertDTO.builder()
                .userAlertId(entity.getUserAlertId())
                .user(entity.getUser() != null ? UserDTO.toDTO(entity.getUser()) : null) // ✅ UserDTO 변환
                .isRead(entity.getIsRead())
                .build();
    }
}