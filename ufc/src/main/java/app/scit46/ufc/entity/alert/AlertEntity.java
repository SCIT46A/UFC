package app.scit46.ufc.entity.alert;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import app.scit46.ufc.dto.alert.AlertDTO;

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

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "link_url", length = 255)
    private String linkUrl;

    @Column(name = "alert_date", nullable = false)
    private LocalDateTime alertDate;

    @OneToMany(mappedBy = "alert", fetch = FetchType.LAZY)
    private List<UserAlertEntity> userAlerts;

    public static AlertEntity toEntity(AlertDTO dto) {
        return AlertEntity.builder()
                .alertId(dto.getAlertId())
                .content(dto.getContent())
                .alertType(dto.getAlertType())
                .imageUrl(dto.getImageUrl())
                .linkUrl(dto.getLinkUrl())
                .alertDate(dto.getAlertDate())
                .build();
    }
}
