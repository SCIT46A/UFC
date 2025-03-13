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
    private ImageUrlDTO photo; // ✅ PhotoDTO 포함

    public static MaterialDTO toDTO(MaterialEntity entity) {
        if (entity == null) return null;
        
        return MaterialDTO.builder()
            .materialId(entity.getMaterialId())
            .name(entity.getName())
            .photo(entity.getPhoto() != null ? ImageUrlDTO.toDTO(entity.getPhoto()) : null) // ✅ PhotoDTO 변환
            .build();
    }
}