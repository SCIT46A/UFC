package app.scit46.ufc.service.product;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.ImageUrlDTO;
import app.scit46.ufc.dto.ItemDTO;
import app.scit46.ufc.dto.custom.GenerateProduct;
import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.ItemEntity;
import app.scit46.ufc.entity.product.ProductEntity;
import app.scit46.ufc.repository.ProductRepository;
import app.scit46.ufc.service.CreatorService;
import app.scit46.ufc.service.ImageUrlService;
import app.scit46.ufc.service.ItemService;
import app.scit46.ufc.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ItemService itemService;
    private final ProductRepository productRepository;
    private final ImageUrlService imageUrlService;
    private final UserService userService;
    private final CreatorService creatorService;

    public String registProduct(GenerateProduct generateProduct) {
        ItemDTO itemDTO = ItemDTO.builder()
            .name(generateProduct.getTitle())
            .description(generateProduct.getDescription())
            .photo(ImageUrlDTO.toDTO(imageUrlService.findByImageId(generateProduct.getImageId())))
            .build();
        ItemEntity item = itemService.addItem(itemDTO);

        ProductEntity product = ProductEntity.builder()
            .item(item)

            .stockQuantity(generateProduct.getStock())
            .createdBy(creatorService.findByOwnUser(userService.findUserByUserName(generateProduct.getUserName().trim())))
            .build();
        productRepository.save(product);
        
        return "success";
    }

}
