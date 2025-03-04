package app.scit46.ufc.service.product;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.ImageUrlDTO;
import app.scit46.ufc.dto.ItemDTO;
import app.scit46.ufc.dto.custom.GenerateProductDTO;
import app.scit46.ufc.dto.product.ProductDTO;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.ImageUrlEntity;
import app.scit46.ufc.entity.ItemEntity;
import app.scit46.ufc.entity.product.ProductEntity;
import app.scit46.ufc.repository.ItemRepository;
import app.scit46.ufc.repository.ProductRepository;
import app.scit46.ufc.service.CreatorService;
import app.scit46.ufc.service.ImageUrlService;
import app.scit46.ufc.service.ItemService;
import app.scit46.ufc.service.UserService;
import app.scit46.ufc.service.tag.TagService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final EntityManager entityManager;
    private final ItemService itemService;
    private final ProductRepository productRepository;
    private final ImageUrlService imageUrlService;
    private final UserService userService;
    private final CreatorService creatorService;
    private final TagService tagService;
    private final ItemRepository itemRepository;
    
    public ProductEntity saveProduct(ProductEntity product) {
        return productRepository.save(product);
    }

    @Transactional
    public Long registProduct(GenerateProductDTO cpDTO) {
        
        // ImageUrlEntity image = entityManager.getReference(ImageUrlEntity.class, cpDTO.getImageId());
        ImageUrlEntity image = imageUrlService.findByImageId(cpDTO.getImageId());
        Long id = image.getId();
        image = imageUrlService.findImage(id);

        ItemEntity item = new ItemEntity();
        item.setName(cpDTO.getTitle());
        item.setDescription(cpDTO.getDescription());

        if(image != null) {
            item.setPhoto(image);
        }

        item = itemRepository.save(item);

        ProductEntity product = ProductEntity.builder()
            .item(item)
            .price(cpDTO.getPrice())
            .stockQuantity(cpDTO.getStock())
            .createdBy(creatorService.findByOwnUser(userService.findUserByUserName(cpDTO.getUserName().trim())))
            .build();
        product = saveProduct(product);

        tagService.linkProductTags(cpDTO.getTagList(), product);
        
        return product.getProductId();
    }

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

}
