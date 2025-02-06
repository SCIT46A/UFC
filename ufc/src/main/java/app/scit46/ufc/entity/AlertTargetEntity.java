package app.scit46.ufc.entity;

import app.scit46.ufc.dto.AlertTargetDTO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "AlertTarget")
public class AlertTargetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_target_id")
    private Long alertTargetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id", nullable = false)
    private AlertEntity alert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_alert_id", nullable = false)
    private UserAlertEntity userAlert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_campaign")
    private CampaignEntity targetCampaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_product")
    private ProductEntity targetProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_badge")
    private BadgeEntity targetBadge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_notice")
    private NoticeEntity targetNotice;

    public static AlertTargetEntity toEntity(AlertTargetDTO dto, AlertEntity alert, UserAlertEntity userAlert,
                                             CampaignEntity targetCampaign, ProductEntity targetProduct,
                                             BadgeEntity targetBadge, NoticeEntity targetNotice) {
        return AlertTargetEntity.builder()
                .alertTargetId(dto.getAlertTargetId())
                .alert(alert)
                .userAlert(userAlert)
                .targetCampaign(targetCampaign)
                .targetProduct(targetProduct)
                .targetBadge(targetBadge)
                .targetNotice(targetNotice)
                .build();
    }
}
