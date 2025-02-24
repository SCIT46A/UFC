package app.scit46.ufc.dto;

import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.product.ProductDTO;
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
    private UserDTO reportedBy; // ✅ UserDTO 포함
    private LocalDateTime reportedDate;
    private UserDTO user; // ✅ UserDTO 포함
    private CampaignDTO campaign; // ✅ CampaignDTO 포함
    private ProductDTO product; // ✅ ProductDTO 포함
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
                .reportedBy(entity.getReportedBy() != null ? UserDTO.toDTO(entity.getReportedBy()) : null) // ✅ UserDTO 변환
                .reportedDate(entity.getReportedDate())
                .user(entity.getUser() != null ? UserDTO.toDTO(entity.getUser()) : null) // ✅ UserDTO 변환
                .campaign(entity.getCampaign() != null ? CampaignDTO.toDTO(entity.getCampaign()) : null) // ✅ CampaignDTO 변환
                .product(entity.getProduct() != null ? ProductDTO.toDTO(entity.getProduct()) : null) // ✅ ProductDTO 변환
                .userUpdatedAt(entity.getUser() != null ? entity.getUser().getUpdatedAt() : null)
                .statusReason(statusReason)
                .build();
    }
}