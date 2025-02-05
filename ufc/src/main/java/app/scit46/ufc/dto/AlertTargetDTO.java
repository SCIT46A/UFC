package app.scit46.ufc.dto;

import app.scit46.ufc.entity.AlertTargetEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AlertTargetDTO {
    private Long alertTargetId;
    private Long alertId;
    private Long userAlertId;
    private Long targetCampaign;
    private Long targetProduct;
    private Long targetBadge;
    private Integer targetNotice;

    public static AlertTargetDTO toDTO(AlertTargetEntity entity) {
        return AlertTargetDTO.builder()
                .alertTargetId(entity.getAlertTargetId())
                .alertId(entity.getAlert() != null ? entity.getAlert().getAlertId() : null)
                .userAlertId(entity.getUserAlert() != null ? entity.getUserAlert().getUserAlertId() : null)
                .targetCampaign(entity.getTargetCampaign() != null ? entity.getTargetCampaign().getCampaignId() : null)
                .targetProduct(entity.getTargetProduct() != null ? entity.getTargetProduct().getProductId() : null)
                .targetBadge(entity.getTargetBadge() != null ? entity.getTargetBadge().getBadgeId() : null)
                .targetNotice(entity.getTargetNotice() != null ? entity.getTargetNotice().getNoticeId() : null)
                .build();
    }
}
