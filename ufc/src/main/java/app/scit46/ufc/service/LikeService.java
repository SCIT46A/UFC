package app.scit46.ufc.service;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.scit46.ufc.dto.LikeDTO;
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




    //
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
        } else {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
    }

    //알림 전송 목적으로 만듦
    public List<CreatorEntity> findCreatorLikeList(Long userId) {
        List<LikeEntity> likeList = likeRepository.findByUser_UserId(userId);
        return likeList.stream().map(LikeEntity::getCreator).collect(Collectors.toList());
    }

    // 알림 전송 목적으로 만듦
    public List<CampaignEntity> findCampaignLikeList(Long userId) {
        List<LikeEntity> likeList = likeRepository.findByUser_UserId(userId);
        return likeList.stream().map(LikeEntity::getCampaign).collect(Collectors.toList());
    }

    // 알림 전송 목적으로 만듦
    public List<ProductEntity> findProductLikeList(Long userId) {
        List<LikeEntity> likeList = likeRepository.findByUser_UserId(userId);
        return likeList.stream().map(LikeEntity::getProduct).collect(Collectors.toList());
    }
      
    public List<LikeDTO> getLikesByUserId(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return user.getLikes().stream()
                .map(LikeDTO::toDTO)
                .collect(Collectors.toList());
    }

    public Page<LikeDTO> getListByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        return likeRepository.findByUser_UserId(userId, pageable).map(LikeDTO::toDTO);
    }

    public Page<CampaignDTO> getLikedCampaignsByUserId(Long userId, int page, int size) {
    // 좋아요한 캠페인을 조회하는 JPQL 또는 QueryDSL 쿼리를 사용합니다.
    // 예시: LIKE 엔티티와 Campaign 엔티티를 조인하여 userId에 해당하는 캠페인만 반환
    return campaignRepository.findLikedCampaignsByUserId(userId, PageRequest.of(page, size)).map(CampaignDTO::toDTO);
}



public List<LikeDTO> getLikeByUserUserId(Long userId) {
    if (userId == null) {
        return new ArrayList<>(); // userId가 null이면 빈 리스트 반환
    }

    List<LikeEntity> likes = likeRepository.findByUser_UserId(userId);
    
    if (likes == null || likes.isEmpty()) {
        return new ArrayList<>(); // 조회 결과가 없거나 null이면 빈 리스트 반환
    }

    return likes.stream()
            .map(LikeDTO::toDTO)
            .collect(Collectors.toList());
}


public List<LikeDTO> getLikeByCampaignId(Long campaignId) {
    return likeRepository.findByCampaign_CampaignId(campaignId).stream()
            .map(LikeDTO::toDTO)
            .collect(Collectors.toList());
}

    @Transactional
    public void deleteLike(Long likeId, Long userId) {
        LikeEntity like = likeRepository.findById(likeId)
                .orElseThrow(() -> new RuntimeException("해당 좋아요를 찾을 수 없습니다."));
        
        // 현재 사용자와 일치하는지 확인 (권한 체크)
        if (!like.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        
        // 해당 LikeEntity 삭제
        likeRepository.delete(like);
    }

}
