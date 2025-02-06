package app.scit46.ufc.entity;

import app.scit46.ufc.dto.AlertDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Alerts")
public class AlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long alertId;

    @Column(name = "content", nullable = false, length = 255)
    private String content;

    @Column(name = "alert_type", length = 30)
    private String alertType;

    @Column(name = "alert_date", nullable = false)
    private LocalDateTime alertDate;

    // OneToMany: AlertTarget.alert 참조
    @OneToMany(mappedBy = "alert", fetch = FetchType.LAZY)
    private List<AlertTargetEntity> alertTargets;

    public static AlertEntity toEntity(AlertDTO dto) {
        return AlertEntity.builder()
                .alertId(dto.getAlertId())
                .content(dto.getContent())
                .alertType(dto.getAlertType())
                .alertDate(dto.getAlertDate())
                .build();
    }
}
