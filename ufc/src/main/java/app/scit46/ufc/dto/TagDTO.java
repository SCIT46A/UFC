package app.scit46.ufc.dto;

import app.scit46.ufc.entity.TagEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TagDTO {
    private Integer tagId;
    private String content;

    public static TagDTO toDTO(TagEntity entity) {
        return TagDTO.builder()
                .tagId(entity.getTagId())
                .content(entity.getContent())
                .build();
    }
}
