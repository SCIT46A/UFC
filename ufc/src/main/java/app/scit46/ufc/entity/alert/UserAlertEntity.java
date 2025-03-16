package app.scit46.ufc.entity.alert;

import java.time.LocalDateTime;

import app.scit46.ufc.dto.alert.UserAlertDTO;
import app.scit46.ufc.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "UserAlerts")
public class UserAlertEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_alert_id")
    private Long userAlertId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id")
    private AlertEntity alert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 알림 읽은 시간 -> 알림 읽은 시간이 없으면 알림 읽지 않은 것으로 판단, 읽은 후 3일 뒤 삭제
    @Column(name = "read_time")
    private LocalDateTime readTime;

    public static UserAlertEntity toEntity(UserAlertDTO dto) {
        return UserAlertEntity.builder()
                .userAlertId(dto.getUserAlertId())
                .alert(AlertEntity.toEntity(dto.getAlert()))
                .user(UserEntity.toEntity(dto.getUser()))
                .build();
    }
}