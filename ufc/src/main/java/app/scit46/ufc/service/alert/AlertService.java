package app.scit46.ufc.service.alert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.alert.AlertDTO;
import app.scit46.ufc.entity.BadgeEntity;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.LikeEntity;
import app.scit46.ufc.entity.MaterialDonationEntity;
import app.scit46.ufc.entity.NoticeEntity;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.entity.alert.AlertEntity;
import app.scit46.ufc.entity.campaign.CampaignBoardEntity;
import app.scit46.ufc.entity.campaign.CampaignEntity;
import app.scit46.ufc.entity.product.ProductDeliveryEntity;
import app.scit46.ufc.entity.product.ProductEntity;
import app.scit46.ufc.entity.reward.RewardDeliveryEntity;
import app.scit46.ufc.repository.LikeRepository;
import app.scit46.ufc.repository.MaterialDonationRepository;
import app.scit46.ufc.repository.UserRepository;
import app.scit46.ufc.repository.alert.AlertRepository;
import app.scit46.ufc.service.ImageUrlService;
import app.scit46.ufc.service.cloudflare.ImageService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;
    private final ImageService imageService;
    private final ImageUrlService imageUrlService;
    private final UserAlertService userAlertService;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final MaterialDonationRepository materialDonationRepository;

    private AlertEntity createAlert(AlertEntity alert) {
        return alertRepository.save(alert);
    }

    // 알림 등록 / data는 각종 Entity 클래스 인스턴스
    @Async
    public AlertEntity registAlert(Object data, String type) {
        AlertEntity alert;
        Set<UserEntity> targetUsers = new HashSet<>();
        // Admin 공지사항 등록 -> 전체유저
        if (data instanceof NoticeEntity && type.equals("Notice")) {
            NoticeEntity notice = (NoticeEntity) data;
            alert = generateAlert(notice, "Notice");
            targetUsers = new HashSet<>(userRepository.findAll());

            // 창작자 승인 -> 창작자
        } else if (data instanceof CreatorEntity) {
            CreatorEntity creator = (CreatorEntity) data;
            if (type.equals("uAccept")) {
                alert = generateAlert(creator, "uAccept");
                targetUsers.add(userRepository.findById(creator.getOwnUser().getUserId()).get());
            } else {
                throw new IllegalArgumentException("[창작자 등록]지원하지 않는 타입 : " + type);
            }

            // 캠페인 등록 -> 관심유저, 캠페인 승인 / 거절 -> 창작자
        } else if (data instanceof CampaignEntity) {
            CampaignEntity campaign = (CampaignEntity) data;

            if (type.equals("cRegist")) {
                // 캠페인 등록 승인 시 => 창작자, 창작자 관심유저
                alert = generateAlert(campaign, "cRegist");
                // 캠페인을 좋아요 한 유저 list
                targetUsers.addAll(likeRepository.findByCampaign_CampaignId(campaign.getCampaignId()).stream()
                        .map(LikeEntity::getUser).collect(Collectors.toList()));

            } else if (type.equals("cAccept")) {
                // 캠페인 승인시 => 창작자
                alert = generateAlert(campaign, "cAccept");
                // 캠페인 창작자(유저)
                targetUsers.add(userRepository.findById(campaign.getCreatedBy().getOwnUser().getUserId()).get());
            } else if (type.equals("cDecline")) {
                // 캠페인 거절시 => 창작자
                alert = generateAlert(campaign, "cDecline");
                // 캠페인 창작자(유저)
                targetUsers.add(userRepository.findById(campaign.getCreatedBy().getOwnUser().getUserId()).get());
            } else {
                throw new IllegalArgumentException("[캠페인 알림등록]지원하지 않는 타입 : " + type);
            }

            // 새 캠페인 소식 등록 -> 관심유저, 참여유저
        } else if (data instanceof CampaignBoardEntity) {
            CampaignBoardEntity campaignBoard = (CampaignBoardEntity) data;
            alert = generateAlert(campaignBoard, "cNotice");
            // 캠페인 관심유저
            targetUsers.addAll(
                    likeRepository.findByCampaign_CampaignId(campaignBoard.getCampaign().getCampaignId()).stream()
                            .map(LikeEntity::getUser).collect(Collectors.toList()));
            // 캠페인 참여유저
            targetUsers.addAll(materialDonationRepository
                    .findByCampaign_CampaignId(campaignBoard.getCampaign().getCampaignId()).stream()
                    .map(MaterialDonationEntity::getUser).collect(Collectors.toList()));

            // 업적 달성 -> 전체유저(트리거?) / 업적 메시지(Alert)는 사전에 생성 후 업적 달성시 UserAlert에 저장
        } else if (data instanceof BadgeEntity) {
            BadgeEntity badge = (BadgeEntity) data;
            alert = generateAlert(badge, "Badge");

            // 캠페인 참여(펀딩) -> 창작자, 기부자, 참여 승인 / 거절 -> 참여유저
        } else if (data instanceof MaterialDonationEntity) {
            MaterialDonationEntity materialDonation = (MaterialDonationEntity) data;
            if (type.equals("mRegist")) {
                // 참여 등록시
                alert = generateAlert(materialDonation, "mRegist");
                // 참여 기부자
                targetUsers.add(materialDonation.getUser());
                // 캠페인 창작자
                targetUsers.add(userRepository
                        .findById(materialDonation.getCampaign().getCreatedBy().getOwnUser().getUserId()).get());

            } else if (type.equals("mAccept")) {
                // 참여 승인시
                alert = generateAlert(materialDonation, "mAccept");
                // 참여 기부자
                targetUsers.add(materialDonation.getUser());
            } else if (type.equals("mDecline")) {
                // 참여 거절시
                alert = generateAlert(materialDonation, "mDecline");
                // 참여 기부자
                targetUsers.add(materialDonation.getUser());
            } else {
                throw new IllegalArgumentException("[참여 알림등록]지원하지 않는 타입 : " + type);
            }

            // 상품 등록 -> 창작자 관심유저
        } else if (data instanceof ProductEntity) {
            ProductEntity product = (ProductEntity) data;
            alert = generateAlert(product, "pRegist");
            // 창작자 관심유저
            targetUsers.addAll(likeRepository.findByProduct_ProductId(product.getProductId()).stream()
                    .map(LikeEntity::getUser).collect(Collectors.toList()));

        } else {
            throw new IllegalArgumentException("[알림등록]지원하지 않는 데이터 타입 : " + data.getClass().getSimpleName());
        }

        alertRepository.save(alert);
        userAlertService.sendAlert(type, alert, new ArrayList<>(targetUsers));
        // user list 추출 필요
        return alert;
    }

    // 알림 생성
    private AlertEntity generateAlert(Object data, String type) {
        return AlertEntity.builder()
                .content(generateMessage(data, type))
                .alertType(type)
                .imageUrl(imageService.getImageUrl(generateImageUrl(data))) // 0번이 기본 이미지
                .linkUrl(generateLinkUrl(data, type))
                .alertDate(LocalDateTime.now())
                .build();
    }

    // 알림 메시지 생성
    private String generateMessage(Object data, String type) {
        String message = "";
        if (data instanceof NoticeEntity) {
            NoticeEntity notice = (NoticeEntity) data;
            message = "새 공지사항 : " + notice.getTitle();
        } else if (data instanceof CampaignEntity && type.equals("cRegist")) {
            CampaignEntity campaign = (CampaignEntity) data;
            message = "새로운 캠페인 : " + campaign.getTitle();
        } else if (data instanceof CampaignEntity && type.equals("cAccept")) {
            CampaignEntity campaign = (CampaignEntity) data;
            message = "캠페인 등록 승인 : " + campaign.getTitle();
        } else if (data instanceof CampaignEntity && type.equals("cDecline")) {
            CampaignEntity campaign = (CampaignEntity) data;
            message = "캠페인 등록 거절: " + campaign.getTitle();
        } else if (data instanceof CampaignBoardEntity) {
            CampaignBoardEntity campaignBoard = (CampaignBoardEntity) data;
            message = "캠페인 새 소식 : " + campaignBoard.getTitle();
        } else if (data instanceof BadgeEntity) {
            BadgeEntity badge = (BadgeEntity) data;
            message = "업적 달성: " + badge.getName();
        } else if (data instanceof MaterialDonationEntity && type.equals("mRegist")) {
            MaterialDonationEntity materialDonation = (MaterialDonationEntity) data;
            message = "검토 대기중인 자원 기부 : " + materialDonation.getCampaign().getTitle();
        } else if (data instanceof MaterialDonationEntity && type.equals("mAccept")) {
            MaterialDonationEntity materialDonation = (MaterialDonationEntity) data;
            message = "자원 기부 승인: " + materialDonation.getCampaign().getTitle();
        } else if (data instanceof MaterialDonationEntity && type.equals("mDecline")) {
            MaterialDonationEntity materialDonation = (MaterialDonationEntity) data;
            message = "자원 기부 거절: " + materialDonation.getCampaign().getTitle();
        } else if (data instanceof ProductEntity) {
            ProductEntity product = (ProductEntity) data;
            message = "창작자의 새 상품 : " + product.getItem().getName();
        } else if (data instanceof RewardDeliveryEntity) {
            RewardDeliveryEntity rewardDelivery = (RewardDeliveryEntity) data;
            message = "리워드 배송 준비 : " + rewardDelivery.getInvoice();
        } else if (data instanceof ProductDeliveryEntity) {
            ProductDeliveryEntity productDelivery = (ProductDeliveryEntity) data;
            message = "상품 배송 준비 : " + productDelivery.getInvoice();
        }

        else {
            throw new IllegalArgumentException("[메시지 생성]지원하지 않는 데이터 타입 : " + data.getClass().getSimpleName());
        }
        return message;
    }

    // 알림 이동 링크 URL 생성
    private String generateLinkUrl(Object data, String type) {
        String linkUrl = "https://upda.store";
        if (data instanceof NoticeEntity) {
            NoticeEntity notice = (NoticeEntity) data;
            linkUrl = "";
            // linkUrl += "/notice/" + notice.getNoticeId();
        } else if (data instanceof CampaignEntity) {
            CampaignEntity campaign = (CampaignEntity) data;
            linkUrl += "/campaign/" + campaign.getCampaignId();
        } else if (data instanceof CampaignBoardEntity) {
            CampaignBoardEntity campaignBoard = (CampaignBoardEntity) data;
            linkUrl += "/campaign/" + campaignBoard.getCampaign().getCampaignId();
        } else if (data instanceof BadgeEntity) {
            // BadgeEntity badge = (BadgeEntity) data;
            linkUrl += "/user/badge";
        } else if (data instanceof MaterialDonationEntity) {
            MaterialDonationEntity materialDonation = (MaterialDonationEntity) data;
            linkUrl += "/user/donation/detail/" + materialDonation.getDonationId();
        } else if (data instanceof ProductEntity) {
            linkUrl = "";
            // TODO: 마이페이지 상품 결제내역 미구현으로 메인으로 이동 처리
            // ProductEntity product = (ProductEntity) data;
            // linkUrl += "/user/product/detail/" + product.getItem().getItemId();
        } else if (data instanceof RewardDeliveryEntity) {
            RewardDeliveryEntity rewardDelivery = (RewardDeliveryEntity) data;
            linkUrl += "/user/donation/detail/" + rewardDelivery.getDonation().getDonationId();
        } else if (data instanceof ProductDeliveryEntity) {
            linkUrl = "";
            // TODO: 마이페이지 상품 결제내역 미구현으로 메인으로 이동 처리
            // ProductDeliveryEntity productDelivery = (ProductDeliveryEntity) data;
            // linkUrl += "/productDelivery/" +
            // productDelivery.getProduct().getItem().getItemId();
        }
        return linkUrl;
    }

    // 알림 이미지 URL 생성
    private String generateImageUrl(Object data) {
        if (data instanceof NoticeEntity) {
            return imageUrlService.findImage(0L); // 0번이 기본 이미지
        } else if (data instanceof CampaignEntity) {
            return imageUrlService.findImage(((CampaignEntity) data).getPhoto().getId());
        } else if (data instanceof CampaignBoardEntity) {
            return imageUrlService.findImage(((CampaignBoardEntity) data).getCampaign().getPhoto().getId());
        } else if (data instanceof BadgeEntity) {
            return imageUrlService.findImage(((BadgeEntity) data).getPhoto().getId());
        } else if (data instanceof MaterialDonationEntity) {
            return imageUrlService.findImage(((MaterialDonationEntity) data).getCampaign().getPhoto().getId());
        } else if (data instanceof ProductEntity) {
            return imageUrlService.findImage(((ProductEntity) data).getItem().getPhoto().getId());
        } else {
            throw new IllegalArgumentException("[이미지 URL 생성]지원하지 않는 데이터 타입 : " + data.getClass().getSimpleName());
        }
    }

}
