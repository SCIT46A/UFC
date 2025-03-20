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
    private UserDTO user; // ✅ UserDTO 포함
    private BadgeDTO badge; // ✅ BadgeDTO 포함

    public static UserBadgeDTO toDTO(UserBadgeEntity entity) {
        return UserBadgeDTO.builder()
                .userBadgeId(entity.getUserBadgeId())
                .user(entity.getUser() != null ? UserDTO.toDTO(entity.getUser()) : null) // ✅ UserDTO 변환
                .badge(entity.getBadge() != null ? BadgeDTO.toDTO(entity.getBadge()) : null) // ✅ BadgeDTO 변환
                .build();
    }
}