package app.scit46.ufc.service.campaign;

import app.scit46.ufc.dto.campaign.CampaignBoardDTO;
import app.scit46.ufc.entity.campaign.CampaignBoardEntity;
import app.scit46.ufc.entity.campaign.CampaignBoardReplyEntity;
import app.scit46.ufc.repository.campaign.CampaignBoardRepository;
import app.scit46.ufc.repository.campaign.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignBoardService {

    private final CampaignBoardRepository campaignBoardRepository;

    private final CampaignRepository campaignRepository;


    public Long boradsave(Long campaignId, String content, String title) {
        CampaignBoardEntity board = new CampaignBoardEntity();
        board.setTitle(title);
        board.setContent(content);
        board.setCampaign(campaignRepository.findById(campaignId).orElse(null));
        campaignBoardRepository.save(board);
        return board.getCBoardId();
    }

    // 수정(업데이트) 메서드
    public Long updateBoard(Long campaignId, Long boardId, String content, String title, UserDetails userDetails) {
        CampaignBoardEntity board = campaignBoardRepository.findById(boardId).orElse(null);
        if(board == null) {
            // 존재하지 않는 경우에 대한 처리 (예외 발생 또는 null 반환 등)
            throw new IllegalArgumentException("Board not found");
        }
        board.setContent(content);
        board.setTitle(title);
        campaignBoardRepository.save(board);
        return board.getCBoardId();
    }


    // 전체 board 리스트 조회 메서드
    public List<CampaignBoardDTO> getCampaignBoardList(Long campaignId) {
        List<CampaignBoardEntity> entities = campaignBoardRepository.findAllByCampaign_CampaignId(campaignId);
        return entities.stream()
                .map(CampaignBoardDTO::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<CampaignBoardDTO> getCampaignBoard(Long boardId) {
        return campaignBoardRepository.findById(boardId).map(CampaignBoardDTO::toDTO);
    }

    @Transactional
    public boolean deleteBoard(Long boardId) {
        try {
            // 게시글 존재 여부를 먼저 확인할 수 있음 (필요에 따라)
            if (!campaignBoardRepository.existsById(boardId)) {
                System.err.println("존재하지 않는 게시글 ID: " + boardId);
                return false;
            }
            campaignBoardRepository.deleteById(boardId);
            return true;
        } catch (Exception e) {
            System.err.println("서비스 삭제 에러: " + e.getMessage());
            return false;
        }
    }




}
