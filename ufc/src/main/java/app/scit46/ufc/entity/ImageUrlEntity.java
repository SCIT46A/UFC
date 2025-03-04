package app.scit46.ufc.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import app.scit46.ufc.dto.ImageUrlDTO;
import app.scit46.ufc.entity.campaign.CampaignEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "ImageUrls")
public class ImageUrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long id;

    @Column(name = "image_id", nullable = false, unique = true, length = 32)
    private String imageId; // uuid

    @Column(name = "filename", nullable = false, unique = true, length = 255)
    private String filename;

    @Column(name = "uploaded_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private UserEntity uploadedBy;

    // OneToMany: Materials.photo_id 참조
    @OneToMany(mappedBy = "photo", fetch = FetchType.LAZY)
    private List<MaterialEntity> materials;

    // OneToMany: Items.photo_id 참조
    @OneToMany(mappedBy = "photo", fetch = FetchType.LAZY)
    private List<ItemEntity> items;

    // OneToMany: Campaigns.photo_id 참조
    @OneToMany(mappedBy = "photo", fetch = FetchType.LAZY)
    private List<CampaignEntity> campaigns;

    public static ImageUrlEntity toEntity(ImageUrlDTO dto) {
        return builder()
                // .id(dto.getId()) // 기본값 자동 생성이므로 주석처리
                .imageId(dto.getImageId())
                .filename(dto.getFilename())
                // .uploadedAt(dto.getUploadedAt()) // 기본값 설정
                .uploadedBy(UserEntity.builder().userId(dto.getUploadedBy()).build()) // userId만 담고 있는 Entity로 변환
                .build();
    }
}
