package app.scit46.ufc.dto;

import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.product.ProductDTO;
import app.scit46.ufc.entity.LikeEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class LikeDTO {
    private Long likeId;
    private UserDTO user; // ✅ UserDTO 포함
    private CreatorDTO creator; // ✅ CreatorDTO 포함
    private CampaignDTO campaign; // ✅ CampaignDTO 포함
    private ProductDTO product; // ✅ ProductDTO 포함

    public static LikeDTO toDTO(LikeEntity entity) {
        return LikeDTO.builder()
                .likeId(entity.getLikeId())
                .user(entity.getUser() != null ? UserDTO.toDTO(entity.getUser()) : null) // ✅ UserDTO 변환
                .creator(entity.getCreator() != null ? CreatorDTO.toDTO(entity.getCreator()) : null) // ✅ CreatorDTO 변환
                .campaign(entity.getCampaign() != null ? CampaignDTO.toDTO(entity.getCampaign()) : null) // ✅ CampaignDTO 변환
                .product(entity.getProduct() != null ? ProductDTO.toDTO(entity.getProduct()) : null) // ✅ ProductDTO 변환
                .build();
    }
}