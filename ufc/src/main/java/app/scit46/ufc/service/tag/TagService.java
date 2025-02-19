package app.scit46.ufc.service.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
    
    
}
