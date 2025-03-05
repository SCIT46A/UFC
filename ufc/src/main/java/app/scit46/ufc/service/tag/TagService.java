package app.scit46.ufc.service.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.TagDTO;
import app.scit46.ufc.entity.TagEntity;
import app.scit46.ufc.repository.tag.TagRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public List<Integer> saveAndFindTagIds(List<String> tagList){
        List<TagEntity> tags = tagList.stream()
                .map(tag -> TagEntity.builder()
                        .content(tag)
                        .build())
                .collect(Collectors.toList());
        List<Integer> idList = new ArrayList<>();

        tags.forEach(tag -> idList.add(tagRepository.save(tag).getTagId()));
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
    
    
}
