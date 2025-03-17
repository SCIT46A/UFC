package app.scit46.ufc.dto.alert;

import java.time.LocalDateTime;

import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.entity.alert.UserAlertEntity;
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
public class UserAlertDTO {
    private Long userAlertId;
    private AlertDTO alert;
    private UserDTO user;
    private LocalDateTime readTime;

    public static UserAlertDTO toDTO(UserAlertEntity entity) {
        return UserAlertDTO.builder()
                .userAlertId(entity.getUserAlertId())
                .alert(AlertDTO.toDTO(entity.getAlert()))
                .user(UserDTO.toDTO(entity.getUser()))
                .readTime(entity.getReadTime())
                .build();
    }
}
