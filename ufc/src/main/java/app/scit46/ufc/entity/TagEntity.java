package app.scit46.ufc.entity;

import java.util.List;

import app.scit46.ufc.dto.TagDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "Tags")
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Integer tagId;

    @Column(name = "content", nullable = false, unique = true, length = 20)
    private String content;

    // OneToMany: CampaignTags.tag 참조
    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
    private List<CampaignTagEntity> campaignTags;

    // OneToMany: ProductTags.tag 참조
    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
    private List<ProductTagEntity> productTags;

    public static TagEntity toEntity(TagDTO dto) {
        return TagEntity.builder()
                //.tagId(dto.getTagId())  // 기본값 자동 생성이므로 주석처리
                .content(dto.getContent())
                .build();
    }
}
