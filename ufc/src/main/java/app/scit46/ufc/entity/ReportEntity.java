package app.scit46.ufc.entity;

import app.scit46.ufc.dto.ReportDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    public static ReportEntity toEntity(ReportDTO dto, UserEntity reportedBy, UserEntity user, CampaignEntity campaign, ProductEntity product) {
        return ReportEntity.builder()
                .reportId(dto.getReportId())
                .status(dto.getStatus())
                .reason(dto.getReason())
                .reportedBy(reportedBy)
                .reportedDate(dto.getReportedDate())
                .user(user)
                .campaign(campaign)
                .product(product)
                .build();
    }
}
