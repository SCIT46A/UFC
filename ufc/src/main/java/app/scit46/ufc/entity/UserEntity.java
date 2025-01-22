package app.scit46.ufc.entity;

import java.time.LocalDateTime;

import app.scit46.ufc.dto.UserDTO;
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
@Table(name = "Users")
public class UserEntity {
    @Id
    @Column(name = "user_id")
    private String userId;
    @Column(name = "username")
    private String username;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "roles")
    private String roles;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static UserEntity toEntity(UserDTO userDTO) {
        return UserEntity.builder()
            .userId(userDTO.getUserId())
            .username(userDTO.getUsername())
            .email(userDTO.getEmail())
            .password(userDTO.getPassword())
            .roles(userDTO.getRoles())
            .createdAt(userDTO.getCreatedAt())
            .updatedAt(userDTO.getUpdatedAt())
            .build();
    }
}
