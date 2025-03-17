package app.scit46.ufc.service.product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.scit46.ufc.dto.product.ProductDTO;
import app.scit46.ufc.entity.product.ProductEntity;
import app.scit46.ufc.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Map<String, Object>> getProductsByCreator(Long creatorId) {
        List<Object[]> resultList = productRepository.findProductsByCreatorId(creatorId);

        if (resultList == null || resultList.isEmpty()) {
            return new ArrayList<>(); // ✅ null 반환 방지
        }

        return resultList.stream().map(obj -> {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("productId", obj[0]); // ✅ 상품 ID
            productMap.put("itemName", obj[1]); // ✅ 상품명
            productMap.put("stockQuantity", obj[2]); // ✅ 재고 수량
            productMap.put("status", convertStatus(obj[3])); // ✅ 상태 변환
            productMap.put("createdTime", obj[4] != null ? obj[4].toString() : "날짜 없음"); // ✅ 날짜 변환

            // ✅ 태그 리스트 변환 (태그가 없으면 빈 리스트 반환)
            String tagString = obj[5] != null ? obj[5].toString().trim() : "";
            List<String> tagList = tagString.isEmpty() ? new ArrayList<>() : Arrays.asList(tagString.split(","));
            productMap.put("tags", tagList);

            return productMap;
        }).collect(Collectors.toList());
    }

    private String convertStatus(Object statusObj) {
        if (statusObj instanceof Number) {
            int status = ((Number) statusObj).intValue();
            switch (status) {
                case 0:
                    return "판매중";
                case 1:
                    return "판매대기";
                case 2:
                    return "품절";
                case 3:
                    return "판매중지";
                default:
                    return "알 수 없음";
            }
        }
        return "알 수 없음";
    }


    public List<ProductDTO> getProductById(Long productId) {
        return productRepository.findById(productId).stream()
                .map(ProductDTO::toDTO)
                .collect(Collectors.toList());
    }
    public ProductDTO findProductById(Long id) {
        return productRepository.findById(id).stream().map(ProductDTO::toDTO).findFirst().orElse(null);
    }

    public Long productBoardSave(Long productId, String content) {
        // 기존 엔티티를 직접 조회
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        // content 필드 업데이트
        productEntity.setContent(content);
        // 엔티티를 저장 (업데이트)
        productRepository.save(productEntity);
        return productEntity.getProductId();
    }

    @Transactional
    public ProductDTO updateProductContent(Long productId, String content) {
        // 기존 엔티티를 DB에서 조회
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("제품을 찾을 수 없습니다."));
        // content 필드 업데이트
        productEntity.setContent(content);
        // 업데이트 후 저장 (업데이트가 트랜잭션 내에서 반영됨)
        productRepository.save(productEntity);
        // DTO 변환 후 반환
        return ProductDTO.toDTO(productEntity);
    }

}
