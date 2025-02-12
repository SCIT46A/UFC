package app.scit46.ufc.dto;

import app.scit46.ufc.entity.CampaignEntity;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CampaignDTO {
    private Long campaignId;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdDate;
    // createdBy와 photo는 필요에 따라 DTO 또는 ID(Long)로 처리 가능
    private Long createdById;
    private Boolean isSuccess;
    private Long photoId;
    private Boolean campaignStatus;

    public static CampaignDTO toDTO(CampaignEntity entity) {
        return CampaignDTO.builder()
                .campaignId(entity.getCampaignId())
                .title(entity.getTitle())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .createdDate(entity.getCreatedDate())
                .isSuccess(entity.getIsSuccess())
                .createdById(entity.getCreatedBy() != null ? entity.getCreatedBy().getCreatorId() : null)
                .photoId(entity.getPhoto() != null ? entity.getPhoto().getPhotoId() : null)
                .campaignStatus(entity.getCampaignStatus())
                .build();
    }
}
