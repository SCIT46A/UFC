package app.scit46.ufc.entity.alert;

import app.scit46.ufc.dto.BadgeDTO;
import app.scit46.ufc.dto.NoticeDTO;
import app.scit46.ufc.dto.alert.AlertDTO;
import app.scit46.ufc.dto.alert.AlertTargetDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.product.ProductDTO;
import app.scit46.ufc.entity.BadgeEntity;
import app.scit46.ufc.entity.NoticeEntity;
import app.scit46.ufc.entity.UserAlertEntity;
import app.scit46.ufc.entity.campaign.CampaignEntity;
import app.scit46.ufc.entity.product.ProductEntity;
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

    public static AlertTargetEntity toEntity(AlertTargetDTO dto, AlertDTO alert, UserAlertEntity userAlert,
                                             CampaignDTO targetCampaign, ProductDTO targetProduct,
                                             BadgeDTO targetBadge, NoticeDTO targetNotice) {
        return AlertTargetEntity.builder()
                .alertTargetId(dto.getAlertTargetId())
                .alert(AlertEntity.builder().alertId(alert.getAlertId()).build())
                .userAlert(UserAlertEntity.builder().userAlertId(userAlert.getUserAlertId()).build())
                .targetCampaign(CampaignEntity.builder().campaignId(targetCampaign.getCampaignId()).build())
                .targetProduct(ProductEntity.builder().productId(targetProduct.getProductId()).build())
                .targetBadge(BadgeEntity.builder().badgeId(targetBadge.getBadgeId()).build())
                .targetNotice(NoticeEntity.builder().noticeId(targetNotice.getNoticeId()).build())
                .build();
    }
}
