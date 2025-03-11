package app.scit46.ufc.service.tag;

import app.scit46.ufc.dto.campaign.CampaignTagDTO;
import app.scit46.ufc.dto.product.ProductTagDTO;
import app.scit46.ufc.entity.campaign.CampaignTagEntity;
import app.scit46.ufc.entity.product.ProductTagEntity;
import app.scit46.ufc.repository.tag.ProductTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductTagService {

    private final ProductTagRepository productTagRepository;


    public List<ProductTagDTO> findTagsByProductId(Long productId) { // 메서드 이름 통일
        List<ProductTagEntity> tagEntities = productTagRepository.findTagsByProduct_ProductId(productId);
        return tagEntities.stream()
                .map(ProductTagDTO::toDTO)
                .collect(Collectors.toList());
    }

}
