package app.scit46.ufc.service.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.TagDTO;
import app.scit46.ufc.dto.custom.GenerateCampaignDTO;
import app.scit46.ufc.entity.TagEntity;
import app.scit46.ufc.entity.campaign.CampaignEntity;
import app.scit46.ufc.entity.campaign.CampaignTagEntity;
import app.scit46.ufc.entity.product.ProductEntity;
import app.scit46.ufc.entity.product.ProductTagEntity;
import app.scit46.ufc.repository.tag.CampaignTagRepository;
import app.scit46.ufc.repository.tag.ProductTagRepository;
import app.scit46.ufc.repository.tag.TagRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final CampaignTagRepository campaignTagRepository;
    private final ProductTagRepository productTagRepository;

    public List<Integer> saveAndFindTagIds(List<String> tagList){
        List<TagEntity> tags = tagList.stream()
                .map(tag -> TagEntity.builder()
                        .content(tag)
                        .build())
                .collect(Collectors.toList());
        List<Integer> idList = new ArrayList<>();

        tags.forEach(tag -> idList.add(tagRepository.findByContent(tag.getContent()).isPresent()
        ?tagRepository.findByContent(tag.getContent()).get().getTagId()
        :tagRepository.save(tag).getTagId()));
        return idList;
    }

    public List<TagDTO> getTopTags() {
        List<Object[]> results = tagRepository.findTopTags();
        List<TagDTO> tags = new ArrayList<>();

        for (Object[] result : results) {
            TagDTO tag = new TagDTO();
            tag.setTagId(((Number) result[0]).intValue()); // tag_id를 Integer로 변환
            tag.setContent((String) result[1]); // content를 String으로 변환
            tag.setTotalUsage(((Number) result[2]).longValue()); // total_usage를 Long으로 변환
            tags.add(tag);
        }

        return tags;
    }

    public void linkCampaignTags(List<String> tagList, CampaignEntity campaignEntity) {
        // 지정된 태그를 먼저 저장/조회 후 태그 아이디 리스트 반환
        List<Integer> tagIds = saveAndFindTagIds(tagList);

        // 태그 아이디와 캠페인 아이디를 CampaignTagEntity(태그 아이디와 캠페인 아이디를 연결하는 엔티티)에 저장
        for (Integer tagId : tagIds) {
            CampaignTagEntity campaignTag = CampaignTagEntity.builder()
                    .campaign(CampaignEntity.builder().campaignId(campaignEntity.getCampaignId()).build())
                    .tag(TagEntity.builder().tagId(tagId).build())
                    .build();

            campaignTagRepository.save(campaignTag);
        }
    }

    public void linkProductTags(List<String> tagList, ProductEntity productEntity) {
        List<Integer> tagIds = saveAndFindTagIds(tagList);

        for (Integer tagId : tagIds) {
            ProductTagEntity productTag = ProductTagEntity.builder()
                    .product(ProductEntity.builder().productId(productEntity.getProductId()).build())
                    .tag(TagEntity.builder().tagId(tagId).build())
                    .build();

            productTagRepository.save(productTag);
        }
    }

    
    
}
