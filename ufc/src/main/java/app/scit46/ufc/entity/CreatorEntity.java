package app.scit46.ufc.entity;

import java.util.List;

import app.scit46.ufc.dto.CreatorDTO;
import app.scit46.ufc.dto.ImageUrlDTO;
import app.scit46.ufc.dto.UserDTO;
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
    private ImageUrlEntity businessCert;

    @Column(name = "b_regist_number", nullable = false, length = 30)
    private String bRegistNumber;

    @Column(name = "b_name", nullable = false, length = 20)
    private String bName;

    @Column(name = "company_name", nullable = false, length = 50)
    private String companyName;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Column(name = "creator_status", nullable = false)
    private Boolean creatorStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "back_img_url")
    private ImageUrlEntity backImgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pro_img_url")
    private ImageUrlEntity proImgUrl;

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
                                         ImageUrlDTO businessCert,
                                         ImageUrlDTO backImgUrl,
                                         ImageUrlDTO proImgUrl,
                                         UserDTO ownUser) {
        return CreatorEntity.builder()
                //.creatorId(dto.getCreatorId())  // 기본값 자동 생성이므로 주석처리
                .intro(dto.getIntro())
                .businessCert(ImageUrlEntity.toEntity(businessCert))
                .bRegistNumber(dto.getBRegistNumber())
                .bName(dto.getBName())
                .companyName(dto.getCompanyName())
                .address(dto.getAddress())
                .creatorStatus(dto.getCreatorStatus())
                .backImgUrl(ImageUrlEntity.toEntity(backImgUrl))
                .proImgUrl(ImageUrlEntity.toEntity(proImgUrl))
                .ownUser(UserEntity.toEntity(ownUser, null)) // photoId는 여기서 필요 없을 듯 ㅎㅎ;;
                .build();
    }
}
