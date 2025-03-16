package app.scit46.ufc.controller;

import app.scit46.ufc.dto.MaterialDonationDTO;
import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.dto.campaign.CampaignDTO;
import app.scit46.ufc.dto.campaign.CampaignGoalDTO;
import app.scit46.ufc.dto.campaign.CampaignTagDTO;
import app.scit46.ufc.dto.product.ProductDTO;
import app.scit46.ufc.dto.product.ProductTagDTO;
import app.scit46.ufc.exception.DBNotFoundException;
import app.scit46.ufc.service.LikeService;
import app.scit46.ufc.service.UserService;
import app.scit46.ufc.service.cloudflare.ImageService;
import app.scit46.ufc.service.product.PayService;
import app.scit46.ufc.service.product.ProductService;
import app.scit46.ufc.service.tag.ProductTagService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    private final ProductTagService productTagService;

    private final ImageService imageService;

    private final LikeService likeService;

    private final UserService userService;

    private final PayService payService;

    @GetMapping("/all")
    public String create() {
        return "product/allProduct";
    }

    @GetMapping("/regist")
    public String regist() {
        return "product/regist-product";
    }

    @GetMapping("/{id}")
    public String detailCampaign(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long loginUserId = null;
        if (session != null) {
            loginUserId = (Long) session.getAttribute("loginUserId");
            model.addAttribute("loginUserId", loginUserId);
        }

        // 제품(캠페인) 조회
        ProductDTO product = productService.findProductById(id);
        Long creatorId = product.getCreatedBy().getOwnUser().getUserId();
        int status = product.getStatus();
        log.info("제품 상태: " + status);

        // status가 0(예: 미승인)인 경우, 로그인한 사용자가 크리에이터와 동일해야 함
        if (status != 1) {
            if (loginUserId == null || !loginUserId.equals(creatorId)) {
                return "redirect:/";
            }
        }

        log.info("제품 상태: " + status);

        // model에 status 값을 그대로 전달 (0,1,2 등)
        model.addAttribute("status", status);

        // 사용 예시
        List<ProductTagDTO> tags = productTagService.findTagsByProductId(id);
        final String DEFAULT_IMAGE = "/static/images/fix/logo.png";

        // 캠페인 이미지 처리
        String imageUrl = (product.getItem().getPhoto() != null)
                ? imageService.getImageUrl(product.getItem().getPhoto().getImageId())
                : DEFAULT_IMAGE;
        model.addAttribute("imageUrl", imageUrl);

        // 크리에이터 이미지 처리
        String creatorImageUrl = (product.getCreatedBy() != null
                && product.getCreatedBy().getBusinessCert() != null)
                        ? imageService.getImageUrl(product.getCreatedBy().getBusinessCert().getImageId())
                        : DEFAULT_IMAGE;
        model.addAttribute("creatorimageUrl", creatorImageUrl);

        // ------

        //
        //
        log.info(product.toString());
        boolean campaignLike = likeService.likeCheck(product.getProductId(), loginUserId, "product");
        boolean creatorLike = likeService.likeCheck(product.getCreatedBy().getCreatorId(), loginUserId, "creator");
        model.addAttribute("campaignLike", campaignLike);
        model.addAttribute("creatorLike", creatorLike);

        model.addAttribute("tags", tags);
        model.addAttribute("product", product);

        return "product/detail-product";
    }

    @GetMapping("/pay")
    public String pay(@RequestParam Integer stock, @RequestParam Long productId, HttpServletRequest request,
            Model model) {
        final String DEFAULT_IMAGE = "/static/images/fix/logo.png";

        ProductDTO product = productService.findProductById(productId);
        log.info(product.toString());
        model.addAttribute("product", product);
        model.addAttribute("stock", stock);

        String imageUrl = (product.getItem().getPhoto() != null)
                ? imageService.getImageUrl(product.getItem().getPhoto().getImageId())
                : DEFAULT_IMAGE;
        model.addAttribute("imageUrl", imageUrl);

        HttpSession session = request.getSession(false); // 세션 가져오기
        Long loginUserId = null;
        if (session != null) {
            Object loginUserObj = session.getAttribute("loginUserId");
            if (loginUserObj != null) {
                if (loginUserObj instanceof Long) {
                    loginUserId = (Long) loginUserObj;
                } else if (loginUserObj instanceof String) {
                    loginUserId = Long.parseLong((String) loginUserObj);
                }
            }
        }
        if (loginUserId != null) {
            try {
                UserDTO user = userService.readUserById(loginUserId);
                model.addAttribute("user", user);
            } catch (DBNotFoundException e) {
                model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
            }
        }

        return "product/pay-product";
    }

    @PostMapping("/pay")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, Object> payload) {
        try {
            payService.gopay(payload); // 전체 payload를 넘김
            return ResponseEntity.ok(Map.of("success", true, "message", "결제 검증 성공"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "결제 검증 실패", "error", e.getMessage()));
        }
    }

}
