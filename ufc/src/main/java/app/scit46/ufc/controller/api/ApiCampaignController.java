package app.scit46.ufc.controller.api;

import app.scit46.ufc.dto.campaign.CampaignBoardDTO;
import app.scit46.ufc.dto.campaign.CampaignBoardReplyDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.campaign.CampaignReviewDTO;
import app.scit46.ufc.dto.custom.GenerateCampaignDTO;
import app.scit46.ufc.dto.reward.RewardDTO;
import app.scit46.ufc.service.RewardService;
import app.scit46.ufc.service.campaign.CampaignBoardReplyService;
import app.scit46.ufc.service.campaign.CampaignBoardService;
import app.scit46.ufc.service.campaign.CampaignReviewService;
import app.scit46.ufc.service.campaign.CampaignService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiCampaignController {

    private final CampaignService campaignService;

    private final CampaignBoardService campaignBoardService;
    private final CampaignBoardReplyService campaignBoardReplyService;
    private final CampaignReviewService campaignReviewService;
    private final RewardService rewardService;

    @PostMapping("/campaign/create")
    public ResponseEntity<Long> createCampaign(@RequestBody GenerateCampaignDTO campaign) {
        
        Long campaignId = campaignService.createCampaign(campaign);

        return ResponseEntity.ok(campaignId);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Long> updateCampaign(@PathVariable Long id, @RequestBody GenerateCampaignDTO campaign){

        campaignService.editCampaign(campaignService.getCampaignById(id), campaign);

        return ResponseEntity.ok(id);
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

//  board 댓글 관련

    @GetMapping("/replys/{boardId}")
    public ResponseEntity<List<CampaignBoardReplyDTO>> replylist(@PathVariable Long boardId) {
        try {
            List<CampaignBoardReplyDTO> replies = campaignBoardReplyService.replylist(boardId);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            System.err.println("댓글 조회 에러: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

//  댓글 작성

    @PostMapping("/replys/add")
    public ResponseEntity<CampaignBoardReplyDTO> createReply(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long loginUserId = (session != null) ? (Long) session.getAttribute("loginUserId") : null;

        try {
            // 클라이언트에서 boardId와 content를 보낸다고 가정
            Long boardId = Long.valueOf(payload.get("boardId").toString());
            String content = payload.get("content").toString();

            CampaignBoardReplyDTO createdReply = campaignBoardReplyService.createReply(boardId,content ,loginUserId);
            return ResponseEntity.ok(createdReply);
        } catch (Exception e) {
            System.err.println("댓글 생성 에러: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

// 리뷰 불러오기

    @GetMapping("/review/{campaignId}")
    public ResponseEntity<List<CampaignReviewDTO>> reviewList(@PathVariable Long campaignId) {
        try {
            List<CampaignReviewDTO> reviews = campaignReviewService.replylist(campaignId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            System.err.println("댓글 조회 에러: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

//  리뷰 작성하기

    @PostMapping("/review/add")
    public ResponseEntity<CampaignReviewDTO> addReview(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long loginUserId = (session != null) ? (Long) session.getAttribute("loginUserId") : null;

        try {
            // 클라이언트에서 boardId와 content를 보낸다고 가정
            Long campaignId = Long.valueOf(payload.get("campaignId").toString());
            String content = payload.get("reviewContent").toString();
            String rating = payload.get("rating").toString();

            CampaignReviewDTO campaignReviewDTO = campaignReviewService.createReview(campaignId, content, rating, loginUserId);
            return ResponseEntity.ok(campaignReviewDTO);
        } catch (Exception e) {
            System.err.println("댓글 생성 에러: " + e.getMessage());
            return ResponseEntity.status(500).build();

        }

    }

//  리워드 목록 가져오기

    @GetMapping("/reward/{campaignId}")
    public List<RewardDTO> getAllRewards(@PathVariable Long campaignId) {
        return rewardService.getRewards(campaignId);
    }

    @GetMapping("/reward/target/{id}")
    public RewardDTO getReward(@PathVariable Long id) {
        return rewardService.getReward(id);
    }



}
