package app.scit46.ufc.dto;

import app.scit46.ufc.entity.BadgeEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BadgeDTO {
    private Long badgeId;
    private String name;
    private String description;
    private ImageUrlDTO photo; // ✅ PhotoDTO 포함

    public static BadgeDTO toDTO(BadgeEntity entity) {
        return BadgeDTO.builder()
                .badgeId(entity.getBadgeId())
                .name(entity.getName())
                .description(entity.getDescription())
                .photo(entity.getPhoto() != null ? ImageUrlDTO.toDTO(entity.getPhoto()) : null) // ✅ PhotoDTO 변환
                .build();
    }
}