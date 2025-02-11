package app.scit46.ufc.entity;

import java.math.BigInteger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Creators")
@Getter
@Setter
public class CreatorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger creator_id; // PK, 자동 증가

    @Column(length = 200)
    private String intro; // 사업자 한마디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_cert", referencedColumnName = "photo_id")
    private PrivatePhotoEntity business_cert; // 사업자 등록증 (FK)

    @Column(nullable = false, length = 30)
    private String b_regist_number; // 사업자 등록번호 (NOT NULL)

    @Column(nullable = false, length = 20)
    private String b_name; // 사업자 이름 (NOT NULL)

    @Column(nullable = false, length = 50)
    private String company_name; // 상호 (NOT NULL)

    @Column(nullable = false, length = 200)
    private String address; // 사업자 주소 (NOT NULL)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "back_img_url", referencedColumnName = "photo_id")
    private PrivatePhotoEntity back_img_url; // 배경사진 (FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pro_img_url", referencedColumnName = "photo_id")
    private PrivatePhotoEntity pro_img_url; // 프로필 사진 (FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "own_user", referencedColumnName = "user_id")
    private UserEntity own_user; // 사용자 아이디 (FK)
}
