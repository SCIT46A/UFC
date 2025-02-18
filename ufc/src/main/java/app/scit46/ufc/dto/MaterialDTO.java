package app.scit46.ufc.dto;

import app.scit46.ufc.entity.MaterialEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MaterialDTO {
    private Long materialId;
    private String name;
    private String description;
    private Long photo;

    public static MaterialDTO toDTO(MaterialEntity entity) {
        return MaterialDTO.builder()
                .materialId(entity.getMaterialId())
                .name(entity.getName())
                .description(entity.getDescription())
                .photo(entity.getPhoto().getId())
                .build();
    }
}
