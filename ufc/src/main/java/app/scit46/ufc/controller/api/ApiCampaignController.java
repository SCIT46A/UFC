package app.scit46.ufc.controller.api;

import app.scit46.ufc.dto.campaign.CampaignBoardDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.repository.campaign.CampaignBoardRepository;
import app.scit46.ufc.service.campaign.CampaignBoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import app.scit46.ufc.dto.custom.GenerateCampaignDTO;
import app.scit46.ufc.service.campaign.CampaignService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiCampaignController {

    private final CampaignService campaignService;

    private final CampaignBoardService campaignBoardService;

    @PostMapping("/campaign/create")    
    public ResponseEntity<Long> createCampaign(@RequestBody GenerateCampaignDTO campaign) {
        Long campaignId = null;
        try{
            campaignId = campaignService.createCampaign(campaign);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
        
        return ResponseEntity.ok(campaignId);
    }

    @PostMapping("/campaign/update/{id}")
    public ResponseEntity<Long> updateCampaign(@PathVariable Long id, @RequestBody GenerateCampaignDTO campaign){
        try{
            campaignService.editCampaign(id, campaign);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
        
        return ResponseEntity.badRequest().body(null);
    }

    // board 신규 저장
    @PostMapping("/{id}/board")
    public ResponseEntity<Long> createBoard(@PathVariable Long id,
                                            @RequestBody CampaignBoardDTO boardRequest,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        CampaignDTO campaign = campaignService.readCampaign(id);
        Long boardId = campaignBoardService.boradsave(id, boardRequest.getContent(), boardRequest.getTitle());
        return ResponseEntity.ok(boardId);
    }

    // 전체 board 불러오기
    @GetMapping("/campaign/board/{id}")
    public ResponseEntity<List<CampaignBoardDTO>> getCampaignBoardList(@PathVariable Long id) {
        List<CampaignBoardDTO> boardList = campaignBoardService.getCampaignBoardList(id);
        return ResponseEntity.ok(boardList);
    }

    // board 수정하기
    @PostMapping("/{id}/board/{boardId}")
    public ResponseEntity<Long> createBoardEdit(@PathVariable Long id,
                                                @PathVariable Long boardId,
                                                @RequestBody CampaignBoardDTO boardRequest,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        CampaignDTO campaign = campaignService.readCampaign(id);
        Long updatedBoardId = campaignBoardService.updateBoard(id, boardId, boardRequest.getContent(), boardRequest.getTitle(), userDetails);
        return ResponseEntity.ok(updatedBoardId);
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<CampaignBoardDTO> getCampaignBoard(@PathVariable Long boardId) {
        Optional<CampaignBoardDTO> board = campaignBoardService.getCampaignBoard(boardId);

        return board.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/board/delete/{boardId}")
    public ResponseEntity<Boolean> deleteCampaignBoard(@PathVariable Long boardId) {
        try {
            boolean deleted = campaignBoardService.deleteBoard(boardId);
            if (deleted) {
                System.out.println("삭제 성공");
                return ResponseEntity.ok(true);
            } else {
                System.err.println("삭제 실패");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
            }
        } catch (Exception e) {
            System.err.println("삭제 에러: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }




}
