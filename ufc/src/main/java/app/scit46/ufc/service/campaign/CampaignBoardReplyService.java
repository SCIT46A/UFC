package app.scit46.ufc.service.campaign;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.scit46.ufc.dto.campaign.CampaignBoardReplyDTO;
import app.scit46.ufc.entity.UserEntity;
import app.scit46.ufc.entity.campaign.CampaignBoardEntity;
import app.scit46.ufc.entity.campaign.CampaignBoardReplyEntity;
import app.scit46.ufc.repository.campaign.CampaignBoardReplyRepository;
import app.scit46.ufc.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignBoardReplyService {

    private final CampaignBoardReplyRepository campaignBoardReplyRepository;

    private final CampaignBoardService campaignBoardService;

    private final UserService userService;


    public List<CampaignBoardReplyDTO> replylist(Long boardId) {
        return campaignBoardReplyRepository.findAllByCampaignBoardCBoardId(boardId).stream().map(CampaignBoardReplyDTO::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public CampaignBoardReplyDTO createReply(Long boardId, String text, Long userId) {
        // 게시글 조회
        CampaignBoardEntity campaignBoardEntity = campaignBoardService.findById(boardId);
        if(campaignBoardEntity == null) {
            throw new RuntimeException("게시글을 찾을 수 없습니다. boardId: " + boardId);
        }

        // 사용자 조회
        UserEntity user = userService.findById(userId);
        if(user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다. userId: " + userId);
        }

        // 새 댓글 엔티티 생성 (기본키는 자동 생성되므로 설정하지 않습니다)
        CampaignBoardReplyEntity replyEntity = new CampaignBoardReplyEntity();
        replyEntity.setContent(text);
        replyEntity.setCampaignBoard(campaignBoardEntity);
        replyEntity.setReplyedBy(user);

        // 저장
        CampaignBoardReplyEntity savedEntity = campaignBoardReplyRepository.save(replyEntity);

        return CampaignBoardReplyDTO.toDTO(savedEntity);
    }




}
