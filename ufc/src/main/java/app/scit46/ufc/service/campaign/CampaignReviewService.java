package app.scit46.ufc.service.campaign;

import app.scit46.ufc.dto.campaign.CampaignBoardReplyDTO;
import app.scit46.ufc.dto.campaign.CampaignReviewDTO;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.entity.campaign.CampaignBoardEntity;
import app.scit46.ufc.entity.campaign.CampaignBoardReplyEntity;
import app.scit46.ufc.entity.campaign.CampaignEntity;
import app.scit46.ufc.entity.campaign.CampaignReviewEntity;
import app.scit46.ufc.repository.campaign.CampaignReviewRepository;
import app.scit46.ufc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignReviewService {

    private final CampaignReviewRepository campaignReviewRepository;

    private final CampaignService campaignService;

    private final UserService userService;

    public List<CampaignReviewDTO> replylist(Long campaignId) {
        return campaignReviewRepository.findAllByCampaignedBy_CampaignId(campaignId).stream()
                .map(CampaignReviewDTO::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CampaignReviewDTO createReview(Long campaignId, String content, String rating, Long loginUserId) {
        CampaignEntity campaignEntity = campaignService.findCampaignById(campaignId);
        if (campaignEntity == null) {
            throw new RuntimeException("게시글을 찾을 수 없습니다. campaignId: " + campaignId);
        }

        UserEntity user = userService.findById(loginUserId);
        if (user == null) {
            throw new RuntimeException("로그인을 해주세요. userId: " + loginUserId);
        }

        // 중복 리뷰 체크
        Optional<CampaignReviewEntity> existingReview = campaignReviewRepository.findByCampaignedByAndReviewedBy(campaignEntity, user);
        if (existingReview.isPresent()) {
            throw new IllegalArgumentException("이미 별점을 주셨습니다");
        }

        // rating이 String 타입으로 넘어온 경우 처리
        Double ratingValue = Double.parseDouble(rating);
        ratingValue = ratingValue * 5;

        // 새 리뷰 엔티티 생성
        CampaignReviewEntity reviewEntity = new CampaignReviewEntity();
        reviewEntity.setContent(content);
        reviewEntity.setRated(ratingValue);
        reviewEntity.setCampaignedBy(campaignEntity);
        reviewEntity.setReviewedBy(user);

        CampaignReviewEntity savedEntity = campaignReviewRepository.save(reviewEntity);

        return CampaignReviewDTO.toDTO(savedEntity);
    }


//    @Transactional
//    public CampaignBoardReplyDTO createReply(Long boardId, String text, Long userId) {
//        // 게시글 조회
//        CampaignBoardEntity campaignBoardEntity = campaignBoardService.findById(boardId);
//        if(campaignBoardEntity == null) {
//            throw new RuntimeException("게시글을 찾을 수 없습니다. boardId: " + boardId);
//        }
//
//        // 사용자 조회
//        UserEntity user = userService.findById(userId);
//        if(user == null) {
//            throw new RuntimeException("사용자를 찾을 수 없습니다. userId: " + userId);
//        }
//
//        // 새 댓글 엔티티 생성 (기본키는 자동 생성되므로 설정하지 않습니다)
//        CampaignBoardReplyEntity replyEntity = new CampaignBoardReplyEntity();
//        replyEntity.setContent(text);
//        replyEntity.setCampaignBoard(campaignBoardEntity);
//        replyEntity.setReplyedBy(user);
//
//        // 저장
//        CampaignBoardReplyEntity savedEntity = campaignBoardReplyRepository.save(replyEntity);
//
//        return CampaignBoardReplyDTO.toDTO(savedEntity);
//    }

//    public List<CampaignReviewDTO> getCampaignReviewsByUserId(Long userId) {
//        List<CampaignReviewEntity> temp = campaignReviewRepository.findByReviewedBy_UserId(userId);
//
//        return temp.stream()
//                .map(CampaignReviewDTO::toDTO)
//                .filter(dto -> dto.getStatus() == true)
//                .collect(Collectors.toList());
//    }

    public Page<CampaignReviewEntity> getListByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        return campaignReviewRepository.findByReviewedBy_UserId(userId, pageable);
    }

    public long getReviewCountByUserId(Long userId) {
        return campaignReviewRepository.countByReviewedBy_UserId(userId);
    }

    @Transactional
    public void delete(Long cReviewId) {
        CampaignReviewEntity campaignReview = campaignReviewRepository.findById(cReviewId)
                .orElseThrow(() -> new RuntimeException("후기를 찾을 수 없습니다."));
        campaignReview.setStatus(false);
        campaignReviewRepository.save(campaignReview);
    }

    // 후기 페이징 조회
    public Page<CampaignReviewEntity> getList(int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("cReviewId"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return campaignReviewRepository.findAll(pageable);
    }

    public List<CampaignReviewDTO> getListByReviewerId(Long reviewerId) {
        return campaignReviewRepository.findByReviewedBy_UserId(reviewerId).stream()
                .map(CampaignReviewDTO::toDTO)
                .collect(Collectors.toList());
    }



}
