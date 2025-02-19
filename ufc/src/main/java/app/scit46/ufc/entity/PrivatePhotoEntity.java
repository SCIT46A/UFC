package app.scit46.ufc.entity;

import app.scit46.ufc.dto.PrivatePhotoDTO;
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
@Table(name = "PrivatePhotos")
public class PrivatePhotoEntity {

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

    // OneToMany: Users.photo_id 참조
    @OneToMany(mappedBy = "photoId", fetch = FetchType.LAZY)
    private List<UserEntity> users;

    // OneToMany: Creators.business_cert 참조
    @OneToMany(mappedBy = "businessCert", fetch = FetchType.LAZY)
    private List<CreatorEntity> creatorBusinessCerts;

    // OneToMany: Creators.back_img_url 참조
    @OneToMany(mappedBy = "backImgUrl", fetch = FetchType.LAZY)
    private List<CreatorEntity> creatorBackImgUrls;

    // OneToMany: Creators.pro_img_url 참조
    @OneToMany(mappedBy = "proImgUrl", fetch = FetchType.LAZY)
    private List<CreatorEntity> creatorProImgUrls;

    // OneToMany: Badges.photo_id 참조
    @OneToMany(mappedBy = "photo", fetch = FetchType.LAZY)
    private List<BadgeEntity> badgePhotos;

    public static PrivatePhotoEntity toEntity(PrivatePhotoDTO dto) {
        return builder()
                .photoId(dto.getPhotoId())
                .originName(dto.getOriginName())
                .convertName(dto.getConvertName())
                .uploadedDate(dto.getUploadedDate())
                .build();
    }
}
