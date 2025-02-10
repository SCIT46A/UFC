package app.scit46.ufc.dto;

import app.scit46.ufc.entity.SearchEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchDTO {
    private Long id;
    private String name;
    private String type;

    public static SearchDTO toEntity(SearchEntity searchEntity) {
        return SearchDTO.builder()
                .id(searchEntity.getId())
                .name(searchEntity.getName())
                .type(searchEntity.getType())
                .build();
    }
}