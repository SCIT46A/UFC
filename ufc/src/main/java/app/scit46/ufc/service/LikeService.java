package app.scit46.ufc.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.LikeDTO;
import app.scit46.ufc.entity.LikeEntity;
import app.scit46.ufc.repository.LikeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    public List<LikeDTO> getLikesByUserId(Long userId) {
        List<LikeEntity> temp = likeRepository.findByUser_UserId(userId);

        return temp.stream()
            .map(LikeDTO::toDTO)
            .collect(Collectors.toList()); 
    }
}
