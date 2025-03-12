package app.scit46.ufc.service.alert;

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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;
    

    private AlertEntity createAlert(AlertEntity alert) {
        return alertRepository.save(alert);
    }
    
    // public List<AlertDTO> getAlertList(Long userId) {
        // 사용자 아이디를 기반으로 해당 사용자에 대한 모든 알림을 조회
        // List<AlertEntity> alertList = alertRepository.findByUser_UserId(userId);

        // // 사용자가 관심을 표한 창작자, 캠페인, 상품 조회 -> 각각 List로 추출
        // List<LikeEntity> likeList = likeRepository.findByUser_UserId(userId);
        // List<CreatorEntity> creatorList = likeList.stream().map(LikeEntity::getCreator).collect(Collectors.toList());
        // List<CampaignEntity> campaignList = likeList.stream().map(LikeEntity::getCampaign).collect(Collectors.toList());
        // List<ProductEntity> productList = likeList.stream().map(LikeEntity::getProduct).collect(Collectors.toList());

        // AlertDTO alertSendDTO = AlertDTO.builder()
        // .message(alertList.getMessage())
        // .imageUrl(alertList.getImageUrl())
        // .linkUrl(alertList.getLinkUrl())
        // .receivedAt(alertList.getReceivedAt())
        // .build();
        // return alertList;
    // }

    // 알림 등록 / data는 각종 Entity 클래스 인스턴스
    public void registAlert(Object data) {
        if(data instanceof NoticeEntity) {  // 공지사항
            NoticeEntity notice = (NoticeEntity) data;
            AlertEntity alert = AlertEntity.builder()
            .content(generateMessage(notice, "notice"))
            .alertType("notice")
            .build();
            createAlert(alert);
        }else if(data instanceof CampaignEntity) {
            CampaignEntity campaign = (CampaignEntity) data;
            AlertEntity alert = AlertEntity.builder()
            .content(generateMessage(campaign, "campaign"))
            .alertType("campaign")
            .build();
            createAlert(alert);
        }else if(data instanceof  CampaignBoardEntity) {
            CampaignBoardEntity campaignBoard = (CampaignBoardEntity) data;
            AlertEntity alert = AlertEntity.builder()
            .content(generateMessage(campaignBoard, "campaignBoard"))
            .alertType("campaignBoard")
            .build();
            createAlert(alert);
        }else if(data instanceof BadgeEntity) {
            BadgeEntity badge =  (BadgeEntity) data;
            AlertEntity alert = AlertEntity.builder()
            .content(generateMessage(badge, "badge"))
            .alertType("badge")
            .build();
            createAlert(alert);
        }else if(data instanceof MaterialDonationEntity) {
            MaterialDonationEntity materialDonation = (MaterialDonationEntity) data;
            AlertEntity alert = AlertEntity.builder()
            .content(generateMessage(materialDonation, "materialDonation"))
            .alertType("materialDonation")
            .build();
            createAlert(alert);
        }else if(data instanceof ProductEntity) {
            ProductEntity product = (ProductEntity) data;
            AlertEntity alert = AlertEntity.builder()
            .content(generateMessage(product, "product"))
            .alertType("product")
            .build();
            createAlert(alert);
        }
        else{ 
            throw new IllegalArgumentException("지원하지 않는 데이터 타입 : " + data.getClass().getSimpleName());
        }
    }

    public String generateMessage(Object data, String type) {
        String message = "";
        if(data instanceof NoticeEntity) {
            NoticeEntity notice = (NoticeEntity) data;
            message = "새 공지사항 : "+notice.getTitle();
        }else if(data instanceof CampaignEntity) {
            CampaignEntity campaign = (CampaignEntity) data;
            message = "새 캠페인 : "+campaign.getTitle();
        }else if(data instanceof CampaignBoardEntity) {
            CampaignBoardEntity campaignBoard = (CampaignBoardEntity) data;
            message = "캠페인 새 소식 : "+campaignBoard.getTitle();
        }else if(data instanceof BadgeEntity) {
            BadgeEntity badge = (BadgeEntity) data;
            message = "새 업적 : "+badge.getName();
        }else if(data instanceof MaterialDonationEntity) {
            MaterialDonationEntity materialDonation = (MaterialDonationEntity) data;
            message = "새 자원 기부 : "+materialDonation.getCampaign().getTitle();
        }else if(data instanceof ProductEntity) {
            ProductEntity product = (ProductEntity) data;
            message = "새 상품 : "+product.getItem().getName();
        }else if(data instanceof RewardDeliveryEntity) {
            RewardDeliveryEntity rewardDelivery = (RewardDeliveryEntity) data;
            message = "리워드 배송 준비 : "+rewardDelivery.getInvoice();
        }else if(data instanceof ProductDeliveryEntity) {
            ProductDeliveryEntity productDelivery = (ProductDeliveryEntity) data;
            message = "상품 배송 준비 : "+productDelivery.getInvoice();
        }

        else{
            throw new IllegalArgumentException("지원하지 않는 데이터 타입 : " + data.getClass().getSimpleName());
        }
        return message;
    }

    public String generateLinkUrl(Object data, String type) {
        String linkUrl = "";
        if(data instanceof NoticeEntity) {
            NoticeEntity notice = (NoticeEntity) data;
            linkUrl += "/notice/"+notice.getNoticeId();
        }else if(data instanceof CampaignEntity) {
            CampaignEntity campaign = (CampaignEntity) data;
            linkUrl += "/campaign/"+campaign.getCampaignId();
        }else if(data instanceof CampaignBoardEntity) {
            CampaignBoardEntity campaignBoard = (CampaignBoardEntity) data;
            linkUrl += "/campaign/"+campaignBoard.getCampaign().getCampaignId();
        }else if(data instanceof BadgeEntity) {
            BadgeEntity badge = (BadgeEntity) data;
            linkUrl += "/badge/"+badge.getBadgeId();
        }else if(data instanceof MaterialDonationEntity) {
            MaterialDonationEntity materialDonation = (MaterialDonationEntity) data;
            linkUrl += "/campaign/"+materialDonation.getCampaign().getCampaignId();
        }else if(data instanceof ProductEntity) {
            ProductEntity product = (ProductEntity) data;
            linkUrl += "/product/"+product.getItem().getItemId();
        }else if(data instanceof RewardDeliveryEntity) {
            RewardDeliveryEntity rewardDelivery = (RewardDeliveryEntity) data;
            linkUrl += "/rewardDelivery/"+rewardDelivery.getReward().getRewardId();
        }else if(data instanceof ProductDeliveryEntity) {
            ProductDeliveryEntity productDelivery = (ProductDeliveryEntity) data;
            linkUrl += "/productDelivery/"+productDelivery.getProduct().getItem().getItemId();
        }
        return linkUrl;
    }

}
