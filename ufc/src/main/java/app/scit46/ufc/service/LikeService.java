package app.scit46.ufc.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.LikeEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.entity.campaign.CampaignEntity;
import app.scit46.ufc.entity.product.ProductEntity;
import app.scit46.ufc.repository.CreatorRepository;
import app.scit46.ufc.repository.LikeRepository;
import app.scit46.ufc.repository.ProductRepository;
import app.scit46.ufc.repository.UserRepository;
import app.scit46.ufc.repository.campaign.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final LikeRepository likeRepository;

    private final UserRepository userRepository;

    private final CampaignRepository campaignRepository;

    private final ProductRepository productRepository;
    private final CreatorRepository creatorRepository;

    public boolean likeCheck(Long targetId, Long userId, String type) {
        // 만약 userId 또는 targetId가 null이면 바로 false 반환
        if(userId == null || targetId == null) {
            return false;
        }

        // userId로 사용자 엔티티 조회 (없으면 예외 발생)
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if ("campaign".equalsIgnoreCase(type)) {
            // targetId로 CampaignEntity 조회
            CampaignEntity campaign = campaignRepository.findById(targetId)
                    .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + targetId));
            // 해당 사용자와 캠페인에 대한 좋아요가 존재하는지 조회
            return likeRepository.findByUserAndCampaign(user, campaign) != null;
        } else if ("creator".equalsIgnoreCase(type)) {
            // targetId로 CreatorEntity 조회
            CreatorEntity creator = creatorRepository.findById(targetId)
                    .orElseThrow(() -> new RuntimeException("Creator not found with id: " + targetId));
            // 해당 사용자와 크리에이터에 대한 좋아요가 존재하는지 조회
            return likeRepository.findByUserAndCreator(user, creator) != null;
        }

        return false;
    }

    public List<CampaignDTO> getCampaignsLikedByUser(Long userId) {
        List<CampaignDTO> likedCampaigns = new ArrayList<>();
        List<LikeEntity> likes = likeRepository.findByUser_userId(userId);
        for (LikeEntity like : likes) {
            CampaignEntity campaign = like.getCampaign();
            CampaignDTO campaignDTO = CampaignDTO.toDTO(campaign);
            likedCampaigns.add(campaignDTO);
        }
        return likedCampaigns;
    }

//     public List<CampaignDTO> getCampaignsLikedByUser(Long userId) {
//     List<LikeEntity> likes = likeRepository.findByUser_UserId(userId);
//     return likes.stream()
//                 .filter(like -> like.getCampaign() != null)
//                 .map(like -> CampaignDTO.toDTO(like.getCampaign()))
//                 .collect(Collectors.toList());
// }



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
        } else if ("creator".equalsIgnoreCase(type)) {
            CreatorEntity creator = creatorRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Creator not found with id: " + itemId));

            LikeEntity like = likeRepository.findByUserAndCreator(user, creator);
            if (like != null) {
                likeRepository.delete(like);
                return false;
            } else {
                LikeEntity newLike = LikeEntity.builder()
                        .user(user)
                        .creator(creator)
                        .build();
                likeRepository.save(newLike);
                return true;
            }
        }
        else {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
    }





}
