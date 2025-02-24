package app.scit46.ufc.service;

import app.scit46.ufc.entity.LikeEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.entity.campaign.CampaignEntity;
import app.scit46.ufc.entity.product.ProductEntity;
import app.scit46.ufc.repository.LikeRepository;
import app.scit46.ufc.repository.ProductRepository;
import app.scit46.ufc.repository.UserRepository;
import app.scit46.ufc.repository.campaign.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    private final UserRepository userRepository;

    private final CampaignRepository campaignRepository;

    private final ProductRepository productRepository;


    public boolean toggleLike(Long itemId, String type, boolean currentState, Long loginUserId) {
        // 로그인한 사용자의 존재 여부 확인
        UserEntity user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + loginUserId));

        if ("campaign".equalsIgnoreCase(type)) {
            CampaignEntity campaign = campaignRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + itemId));

            // Repository 메서드를 통해 userId, campaignId 기반으로 조회
            LikeEntity like = likeRepository.findByUserAndCampaign(user, campaign);
            if (like != null) {
                likeRepository.delete(like);
                return false;
            } else {
                LikeEntity newLike = LikeEntity.builder()
                        .user(user)
                        .campaign(campaign)
                        .build();
                likeRepository.save(newLike);
                return true;
            }
        } else if ("product".equalsIgnoreCase(type)) {
            ProductEntity product = productRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemId));

            LikeEntity like = likeRepository.findByUserAndProduct(user, product);
            if (like != null) {
                likeRepository.delete(like);
                return false;
            } else {
                LikeEntity newLike = LikeEntity.builder()
                        .user(user)
                        .product(product)
                        .build();
                likeRepository.save(newLike);
                return true;
            }
        } else {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
    }




}
