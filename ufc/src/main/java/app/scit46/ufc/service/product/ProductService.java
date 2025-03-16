package app.scit46.ufc.service.product;

import app.scit46.ufc.dto.product.ProductDTO;
import app.scit46.ufc.entity.campaign.CampaignBoardEntity;
import app.scit46.ufc.entity.product.ProductEntity;
import app.scit46.ufc.entity.product.ProductTagEntity;
import app.scit46.ufc.repository.ProductRepository;
import app.scit46.ufc.service.CreatorService;
import app.scit46.ufc.service.ImageUrlService;
import app.scit46.ufc.service.ItemService;
import app.scit46.ufc.service.UserService;
import app.scit46.ufc.service.tag.TagService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import app.scit46.ufc.repository.tag.ProductTagRepository;
import app.scit46.ufc.entity.TagEntity;
import app.scit46.ufc.repository.tag.TagRepository;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.custom.GenerateProductDTO;
import app.scit46.ufc.entity.ImageUrlEntity;
import app.scit46.ufc.entity.ItemEntity;
import app.scit46.ufc.repository.ItemRepository;

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

        // ImageUrlEntity image = entityManager.getReference(ImageUrlEntity.class,
        // cpDTO.getImageId());
        ImageUrlEntity image = imageUrlService.findByImageId(cpDTO.getImageId());
        image = imageUrlService.findImageById(image.getId());

        ItemEntity item = new ItemEntity();
        item.setName(cpDTO.getTitle());
        item.setDescription(cpDTO.getDescription());

        if (image != null) {
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

    private final ProductTagRepository productTagRepository;
    private final TagRepository tagRepository;

    public List<Map<String, Object>> getProductsByCreator(Long creatorId) {
        List<Object[]> resultList = productRepository.findProductsByCreatorId(creatorId);

        if (resultList == null || resultList.isEmpty()) {
            return new ArrayList<>(); // ✅ null 반환 방지
        }

        return resultList.stream().map(obj -> {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("productId", obj[0]); // ✅ 상품 ID
            productMap.put("itemName", obj[1]); // ✅ 상품명
            productMap.put("itemDescription", obj[2]);
            productMap.put("stockQuantity", obj[3]);
            productMap.put("price", obj[4]); // ✅ 가격
            productMap.put("status", obj[5]); // ✅ 상태 변환
            productMap.put("createdTime", obj[6] != null ? obj[6].toString() : "날짜 없음"); // ✅ 날짜 변환

            // ✅ 태그 리스트 변환 (태그가 없으면 빈 리스트 반환)
            String tagString = obj[7] != null ? obj[7].toString().trim() : "";
            List<String> tagList = tagString.isEmpty() ? new ArrayList<>() : Arrays.asList(tagString.split(","));
            productMap.put("tags", tagList);

            return productMap;
        }).collect(Collectors.toList());
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

    @Transactional
    public void updateProductStatus(Long productId, Integer status) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("제품을 찾을 수 없습니다."));
        productEntity.setStatus(status);
        productRepository.save(productEntity);
    }

    @Transactional
    public void updateProduct(Long productId, Map<String, Object> updates) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        updates.forEach((key, value) -> {
            switch (key) {
                case "itemName":
                    product.getItem().setName(value.toString());
                    break;
                case "price":
                    product.setPrice(Integer.parseInt(value.toString()));
                    break;
                case "stockQuantity":
                    product.setStockQuantity(Integer.parseInt(value.toString()));
                    break;
                case "itemDescription":
                    product.getItem().setDescription(value.toString());
                    break;
            }
        });

        productRepository.save(product);
    }

    @Transactional
    public void updateProductTags(Long productId, List<String> newTags) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        // ✅ 기존 태그 조회
        List<ProductTagEntity> existingTags = productTagRepository.findByProductId(productId);

        // ✅ 기존 태그 목록 (문자열 리스트)
        List<String> existingTagContents = existingTags.stream()
                .map(tag -> tag.getTag().getContent())
                .collect(Collectors.toList());

        // ✅ 추가해야 할 태그 목록
        List<String> tagsToAdd = newTags.stream()
                .filter(tag -> !existingTagContents.contains(tag))
                .collect(Collectors.toList());

        // ✅ 삭제해야 할 태그 목록
        List<ProductTagEntity> tagsToRemove = existingTags.stream()
                .filter(tag -> !newTags.contains(tag.getTag().getContent()))
                .collect(Collectors.toList());

        // ✅ 새로운 태그 추가
        for (String tagContent : tagsToAdd) {
            TagEntity tagEntity = tagRepository.findByContent(tagContent)
                    .orElseGet(() -> {
                        TagEntity newTag = new TagEntity();
                        newTag.setContent(tagContent);
                        tagRepository.save(newTag);
                        return newTag;
                    });

            ProductTagEntity productTag = ProductTagEntity.builder()
                    .product(product)
                    .tag(tagEntity)
                    .build();

            productTagRepository.save(productTag);
        }

        // ✅ 삭제할 태그 제거
        productTagRepository.deleteAll(tagsToRemove);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: ID=" + productId));

        // ✅ 실제 삭제 대신 상태를 "삭제됨(4)"으로 변경
        product.setStatus(4);
        productRepository.save(product);
    }

}
