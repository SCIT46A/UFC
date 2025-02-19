package app.scit46.ufc.entity;

import app.scit46.ufc.dto.CreatorDTO;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Creators")
public class CreatorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "intro")
    private String intro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_cert")
    private PrivatePhotoEntity businessCert;

    @Column(name = "b_regist_number", nullable = false, length = 30)
    private String bRegistNumber;

    @Column(name = "b_name", nullable = false, length = 20)
    private String bName;

    @Column(name = "company_name", nullable = false, length = 50)
    private String companyName;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Column(name = "creator_status", nullable = false)
    private Integer creatorStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "back_img_url")
    private PrivatePhotoEntity backImgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pro_img_url")
    private PrivatePhotoEntity proImgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "own_user", nullable = false)
    private UserEntity ownUser;

    // OneToMany: Campaigns.created_by 참조
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<CampaignEntity> campaigns;

    // OneToMany: Products.created_by 참조
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<ProductEntity> products;

    // OneToMany: Likes.creator 참조
    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    private List<LikeEntity> likes;



    public static CreatorEntity toEntity(CreatorDTO dto,
                                         PrivatePhotoEntity businessCert,
                                         PrivatePhotoEntity backImgUrl,
                                         PrivatePhotoEntity proImgUrl,
                                         UserEntity ownUser) {
        return CreatorEntity.builder()
                .creatorId(dto.getCreatorId())
                .intro(dto.getIntro())
                .businessCert(businessCert)
                .bRegistNumber(dto.getBRegistNumber())
                .bName(dto.getBName())
                .companyName(dto.getCompanyName())
                .address(dto.getAddress())
                .creatorStatus(dto.getCreatorStatus())
                .backImgUrl(backImgUrl)
                .proImgUrl(proImgUrl)
                .ownUser(ownUser)
                .build();
    }
}
