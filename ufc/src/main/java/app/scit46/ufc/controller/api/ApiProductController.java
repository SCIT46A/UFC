package app.scit46.ufc.controller.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.scit46.ufc.dto.custom.GenerateProductDTO;
import app.scit46.ufc.service.product.ProductService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ApiProductController {
    
    private final ProductService productService;

    @PostMapping("/regist")
    public String generateProduct(@RequestBody GenerateProductDTO generateProduct) {

        Long productId = productService.registProduct(generateProduct);

        return productId.toString();
    }
}
