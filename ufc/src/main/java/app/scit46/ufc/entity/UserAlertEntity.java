package app.scit46.ufc.entity;

import app.scit46.ufc.dto.UserAlertDTO;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "UserAlert")
public class UserAlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_alert_id")
    private Long userAlertId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "is_read")
    private Boolean isRead;

    // OneToMany: AlertTarget.user_alert 참조
    @OneToMany(mappedBy = "userAlert", fetch = FetchType.LAZY)
    private List<AlertTargetEntity> alertTargets;

    public static UserAlertEntity toEntity(UserAlertDTO dto, UserEntity user) {
        return UserAlertEntity.builder()
                .userAlertId(dto.getUserAlertId())
                .user(user)
                .isRead(dto.getIsRead())
                .build();
    }
}
