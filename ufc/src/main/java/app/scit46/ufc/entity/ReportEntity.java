package app.scit46.ufc.entity;

import java.time.LocalDateTime;

import app.scit46.ufc.dto.ReportDTO;
import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.product.ProductDTO;
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
@Table(name = "Reports")
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "reason", nullable = false, length = 150)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by")
    private UserEntity reportedBy;

    @Column(name = "reported_date", nullable = false)
    private LocalDateTime reportedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = true)
    private CampaignEntity campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    public boolean isValid() {
        return user != null || campaign != null || product != null;
    }

    public static ReportEntity toEntity(ReportDTO dto, UserDTO reportedBy, UserDTO user, CampaignDTO campaign, ProductDTO product) {
        return ReportEntity.builder()
                .reportId(dto.getReportId())
                .status(dto.getStatus())
                .reason(dto.getReason())
                .reportedBy(UserEntity.builder().userId(reportedBy.getUserId()).build())
                .reportedDate(dto.getReportedDate())
                .user(UserEntity.builder().userId(user.getUserId()).build())
                .campaign(CampaignEntity.builder().campaignId(campaign.getCampaignId()).build())
                .product(ProductEntity.builder().productId(product.getProductId()).build())
                .build();
    }
}
