package app.scit46.ufc.service.alert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.alert.AlertDTO;
import app.scit46.ufc.entity.BadgeEntity;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.LikeEntity;
import app.scit46.ufc.entity.MaterialDonationEntity;
import app.scit46.ufc.entity.NoticeEntity;
import app.scit46.ufc.entity.alert.AlertEntity;
import app.scit46.ufc.entity.campaign.CampaignBoardEntity;
import app.scit46.ufc.entity.campaign.CampaignEntity;
import app.scit46.ufc.entity.product.ProductDeliveryEntity;
import app.scit46.ufc.entity.product.ProductEntity;
import app.scit46.ufc.entity.reward.RewardDeliveryEntity;
import app.scit46.ufc.repository.LikeRepository;
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

    private AlertEntity createAlert(AlertEntity alert) {
        return alertRepository.save(alert);
    }

    // public List<AlertDTO> getAlertList(Long userId) {
    // 사용자 아이디를 기반으로 해당 사용자에 대한 모든 알림을 조회
    // List<AlertEntity> alertList = alertRepository.findByUser_UserId(userId);

    // // 사용자가 관심을 표한 창작자, 캠페인, 상품 조회 -> 각각 List로 추출
    // List<LikeEntity> likeList = likeRepository.findByUser_UserId(userId);
    // List<CreatorEntity> creatorList =
    // likeList.stream().map(LikeEntity::getCreator).collect(Collectors.toList());
    // List<CampaignEntity> campaignList =
    // likeList.stream().map(LikeEntity::getCampaign).collect(Collectors.toList());
    // List<ProductEntity> productList =
    // likeList.stream().map(LikeEntity::getProduct).collect(Collectors.toList());

    // AlertDTO alertSendDTO = AlertDTO.builder()
    // .message(alertList.getMessage())
    // .imageUrl(alertList.getImageUrl())
    // .linkUrl(alertList.getLinkUrl())
    // .receivedAt(alertList.getReceivedAt())
    // .build();
    // return alertList;
    // }

    // 알림 등록 / data는 각종 Entity 클래스 인스턴스
    public void registAlert(Object data, String type) {
        AlertEntity alert;
        // Admin 공지사항 등록 -> 전체유저
        if (data instanceof NoticeEntity && type.equals("Notice")) {
            NoticeEntity notice = (NoticeEntity) data;
            alert = generateAlert(notice, "Notice");
            
        // 캠페인 등록 -> 관심유저, 캠페인 승인 / 거절 -> 창작자
        } else if (data instanceof CampaignEntity) {
            CampaignEntity campaign = (CampaignEntity) data;
            
            if(type.equals("Regist")){
                // 캠페인 등록시
                alert = generateAlert(campaign, "Regist");
            }else if(type.equals("Accept")){
                // 캠페인 승인시
                alert = generateAlert(campaign, "Accept");
            }else if(type.equals("Decline")){
                // 캠페인 거절시
                alert = generateAlert(campaign, "Decline");
            }else{
                throw new IllegalArgumentException("[캠페인 알림등록]지원하지 않는 타입 : " + type);
            }
            
        // 새 캠페인 소식 등록 -> 관심유저, 참여유저
        } else if (data instanceof CampaignBoardEntity) {
            CampaignBoardEntity campaignBoard = (CampaignBoardEntity) data;
            alert = generateAlert(campaignBoard, "Notice");

        // 업적 달성 -> 전체유저(트리거?) / 업적 메시지(Alert)는 사전에 생성 후 업적 달성시 UserAlert에 저장
        } else if (data instanceof BadgeEntity) {
            BadgeEntity badge = (BadgeEntity) data;
            alert = generateAlert(badge, "Badge");

        // 캠페인 참여(펀딩) -> 창작자, 참여 승인 / 거절 -> 참여유저
        } else if (data instanceof MaterialDonationEntity) {
            MaterialDonationEntity materialDonation = (MaterialDonationEntity) data;
            if(type.equals("Regist")){
                // 참여 등록시
                alert = generateAlert(materialDonation, "Regist");
            }else if(type.equals("Accept")){
                // 참여 승인시
                alert = generateAlert(materialDonation, "Accept");
            }else if(type.equals("Decline")){
                // 참여 거절시
                alert = generateAlert(materialDonation, "Decline");
            }else{
                throw new IllegalArgumentException("[참여 알림등록]지원하지 않는 타입 : " + type);
            }

        // 상품 등록 -> 창작자 관심유저
        } else if (data instanceof ProductEntity) {
            ProductEntity product = (ProductEntity) data;
            alert = generateAlert(product, "Regist");

        } else {
            throw new IllegalArgumentException("[알림등록]지원하지 않는 데이터 타입 : " + data.getClass().getSimpleName());
        }

        alertRepository.save(alert);
        userAlertService.sendAlert(type, alert, null);
        // user list 추출 필요
    }

    // 알림 생성
    public AlertEntity generateAlert(Object data, String type){
        return AlertEntity.builder()
                .content(generateMessage(data, type))
                .alertType(type)
                .imageUrl(imageService.getImageUrl(generateImageUrl(data))) // 0번이 기본 이미지
                .linkUrl(generateLinkUrl(data, type))
                .alertDate(LocalDateTime.now())
                .build();
    }

    // 알림 메시지 생성
    public String generateMessage(Object data, String type) {
        String message = "";
        if (data instanceof NoticeEntity) {
            NoticeEntity notice = (NoticeEntity) data;
            message = "새 공지사항 : " + notice.getTitle();
        } else if (data instanceof CampaignEntity) {
            CampaignEntity campaign = (CampaignEntity) data;
            message = "새 캠페인 : " + campaign.getTitle();
        } else if (data instanceof CampaignBoardEntity) {
            CampaignBoardEntity campaignBoard = (CampaignBoardEntity) data;
            message = "캠페인 새 소식 : " + campaignBoard.getTitle();
        } else if (data instanceof BadgeEntity) {
            BadgeEntity badge = (BadgeEntity) data;
            message = "새 업적 : " + badge.getName();
        } else if (data instanceof MaterialDonationEntity) {
            MaterialDonationEntity materialDonation = (MaterialDonationEntity) data;
            message = "새 자원 기부 : " + materialDonation.getCampaign().getTitle();
        } else if (data instanceof ProductEntity) {
            ProductEntity product = (ProductEntity) data;
            message = "새 상품 : " + product.getItem().getName();
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
    public String generateLinkUrl(Object data, String type) {
        String linkUrl = "https://upda.store";
        if (data instanceof NoticeEntity) {
            NoticeEntity notice = (NoticeEntity) data;
            linkUrl = "";
            //linkUrl += "/notice/" + notice.getNoticeId();
        } else if (data instanceof CampaignEntity) {
            CampaignEntity campaign = (CampaignEntity) data;
            linkUrl += "/campaign/" + campaign.getCampaignId();
        } else if (data instanceof CampaignBoardEntity) {
            CampaignBoardEntity campaignBoard = (CampaignBoardEntity) data;
            linkUrl += "/campaign/" + campaignBoard.getCampaign().getCampaignId();
        } else if (data instanceof BadgeEntity) {
            //BadgeEntity badge = (BadgeEntity) data;
            linkUrl += "/user/badge";
        } else if (data instanceof MaterialDonationEntity) {
            MaterialDonationEntity materialDonation = (MaterialDonationEntity) data;
            linkUrl += "/user/donation/detail/" + materialDonation.getDonationId();
        } else if (data instanceof ProductEntity) {
            linkUrl = "";
            // TODO: 마이페이지 상품 결제내역 미구현으로 메인으로 이동 처리
            //ProductEntity product = (ProductEntity) data;
            //linkUrl += "/user/product/detail/" + product.getItem().getItemId();
        } else if (data instanceof RewardDeliveryEntity) {
            RewardDeliveryEntity rewardDelivery = (RewardDeliveryEntity) data;
            linkUrl += "/user/donation/detail/" + rewardDelivery.getDonation().getDonationId();
        } else if (data instanceof ProductDeliveryEntity) {
            linkUrl = "";
            // TODO: 마이페이지 상품 결제내역 미구현으로 메인으로 이동 처리
            // ProductDeliveryEntity productDelivery = (ProductDeliveryEntity) data;
            // linkUrl += "/productDelivery/" + productDelivery.getProduct().getItem().getItemId();
        }
        return linkUrl;
    }

    // 알림 이미지 URL 생성
    public String generateImageUrl(Object data){
        if(data instanceof NoticeEntity){
            return imageUrlService.findImage(0L);   //0번이 기본 이미지
        }else if(data instanceof CampaignEntity){
            return imageUrlService.findImage(((CampaignEntity) data).getPhoto().getId());
        }else if(data instanceof CampaignBoardEntity){
            return imageUrlService.findImage(((CampaignBoardEntity) data).getCampaign().getPhoto().getId());
        }else if(data instanceof BadgeEntity){
            return imageUrlService.findImage(((BadgeEntity) data).getPhoto().getId());
        }else if(data instanceof MaterialDonationEntity){
            return imageUrlService.findImage(((MaterialDonationEntity) data).getCampaign().getPhoto().getId());
        }else if(data instanceof ProductEntity){
            return imageUrlService.findImage(((ProductEntity) data).getItem().getPhoto().getId());
        }else{
            throw new IllegalArgumentException("[이미지 URL 생성]지원하지 않는 데이터 타입 : " + data.getClass().getSimpleName());
        }
    }

}
