package app.scit46.ufc.dto;

import app.scit46.ufc.entity.CampaignReviewEntity;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CampaignReviewDTO {
    private Long cReviewId;
    private String content;
    private LocalDateTime createdDate;
    private Double rated;
    private Long reviewedById;
    private Long campaignedById;

    public static CampaignReviewDTO toDTO(CampaignReviewEntity entity) {
        return CampaignReviewDTO.builder()
                .cReviewId(entity.getCReviewId())
                .content(entity.getContent())
                .createdDate(entity.getCreatedDate())
                .rated(entity.getRated())
                .reviewedById(entity.getReviewedBy() != null ? entity.getReviewedBy().getUserId() : null)
                .campaignedById(entity.getCampaignedBy() != null ? entity.getCampaignedBy().getCampaignId() : null)
                .build();
    }
}
