package app.scit46.ufc.dto;

import app.scit46.ufc.entity.TagEntity;
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
public class TagDTO {
    private Integer tagId;
    private String content;

//  header에서 카테고리 생성을 위해 만듬
    private Long totalUsage;

    public static TagDTO toDTO(TagEntity entity) {
        if(entity == null) {
            return null;
        }
        return TagDTO.builder()
                .tagId(entity.getTagId())
                .content(entity.getContent())
                .build();
    }
}
