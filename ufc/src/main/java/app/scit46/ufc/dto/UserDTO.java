package app.scit46.ufc.dto;

import java.time.LocalDateTime;

import app.scit46.ufc.entity.UserEntity;
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
public class UserDTO {
    private String userId;
    private String username;
    private String email;
    private String password;
    private String roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static UserDTO toDTO(UserEntity userEntity) {
        return UserDTO.builder()
            .userId(userEntity.getUserId())
            .username(userEntity.getUsername())
            .email(userEntity.getEmail())
            .password(userEntity.getPassword())
            .roles(userEntity.getRoles())
            .createdAt(userEntity.getCreatedAt())
            .updatedAt(userEntity.getUpdatedAt())
            .build();
    }
}
