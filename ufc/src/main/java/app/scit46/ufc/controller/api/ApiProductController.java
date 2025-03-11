package app.scit46.ufc.controller.api;

import app.scit46.ufc.dto.campaign.CampaignBoardDTO;
import app.scit46.ufc.dto.product.ProductDTO;
import app.scit46.ufc.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/product")
public class ApiProductController {

    private final ProductService productService;

    @PostMapping("/add/{productId}")
    public ResponseEntity<Long> getProduct(@PathVariable Long productId, @RequestBody ProductDTO boardRequest) {
        Long productTargetId = productService.productBoardSave(productId, boardRequest.getContent());

        return ResponseEntity.ok(productTargetId);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long productId) {
        try {
            ProductDTO productDTO = productService.findProductById(productId);
            if (productDTO == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(productDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/update/{productId}")
    public ResponseEntity<ProductDTO> updateProductContent(
            @PathVariable Long productId,
            @RequestBody Map<String, String> payload) {

        String content = payload.get("content");
        ProductDTO updatedProduct = productService.updateProductContent(productId, content);
        return ResponseEntity.ok(updatedProduct);
    }





}
