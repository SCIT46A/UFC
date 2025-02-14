package app.scit46.ufc.dto;

import app.scit46.ufc.entity.ReportEntity;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ReportDTO {
    private Long reportId;
    private String status;
    private String reason;
    private Long reportedById;
    private LocalDateTime reportedDate;
    private Long userId;
    private Long campaignId;
    private Long productId;
    private LocalDateTime userUpdatedAt;
    private String statusReason;

    public static ReportDTO toDTO(ReportEntity entity) {

            String statusReason = (entity.getUser() != null) ? entity.getUser().getStatusReason() : null;

            // ✅ 로그 추가 (statusReason 값 확인)
            System.out.println("🚀 DTO 변환: reportId=" + entity.getReportId() +
                    ", userId=" + (entity.getUser() != null ? entity.getUser().getUserId() : "null") +
                    ", statusReason=" + statusReason);
        return ReportDTO.builder()
                .reportId(entity.getReportId())
                .status(entity.getStatus())
                .reason(entity.getReason())
                .reportedById(entity.getReportedBy() != null ? entity.getReportedBy().getUserId() : null)
                .reportedDate(entity.getReportedDate())
                .userId(entity.getUser() != null ? entity.getUser().getUserId() : null)
                .campaignId(entity.getCampaign() != null ? entity.getCampaign().getCampaignId() : null)
                .productId(entity.getProduct() != null ? entity.getProduct().getProductId() : null)
                .userUpdatedAt(entity.getUser() != null ? entity.getUser().getUpdatedAt() : null)
                .statusReason(statusReason)
                .build();
    }
}
