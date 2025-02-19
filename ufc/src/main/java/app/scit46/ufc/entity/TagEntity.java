package app.scit46.ufc.entity;

import app.scit46.ufc.dto.TagDTO;
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
                .tagId(dto.getTagId())
                .content(dto.getContent())
                .build();
    }
}
