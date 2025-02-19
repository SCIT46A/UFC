package app.scit46.ufc.dto;

import app.scit46.ufc.entity.UserBadgeEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserBadgeDTO {
    private Long userBadgeId;
    private Long userId;
    private Long badgeId;

    public static UserBadgeDTO toDTO(UserBadgeEntity entity) {
        return UserBadgeDTO.builder()
                .userBadgeId(entity.getUserBadgeId())
                .userId(entity.getUser() != null ? entity.getUser().getUserId() : null)
                .badgeId(entity.getBadge() != null ? entity.getBadge().getBadgeId() : null)
                .build();
    }
}
