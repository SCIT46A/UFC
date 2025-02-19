package app.scit46.ufc.entity;

import app.scit46.ufc.dto.PublicPhotoDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "PublicPhotos")
public class PublicPhotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long photoId;

    @Column(name = "origin_name", nullable = false, unique = true, length = 255)
    private String originName;

    @Column(name = "convert_name", nullable = false, unique = true, length = 300)
    private String convertName;

    @Column(name = "uploaded_date", nullable = false)
    private LocalDateTime uploadedDate;

    // OneToMany: Materials.photo_id 참조
    @OneToMany(mappedBy = "photo", fetch = FetchType.LAZY)
    private List<MaterialEntity> materials;

    // OneToMany: Items.photo_id 참조
    @OneToMany(mappedBy = "photo", fetch = FetchType.LAZY)
    private List<ItemEntity> items;

    // OneToMany: Campaigns.photo_id 참조
    @OneToMany(mappedBy = "photo", fetch = FetchType.LAZY)
    private List<CampaignEntity> campaigns;

    public static PublicPhotoEntity toEntity(PublicPhotoDTO dto) {
        return builder()
                .photoId(dto.getPhotoId())
                .originName(dto.getOriginName())
                .convertName(dto.getConvertName())
                .uploadedDate(dto.getUploadedDate())
                .build();
    }
}
