package app.scit46.ufc.service;

import app.scit46.ufc.dto.TagDTO;
import app.scit46.ufc.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

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
