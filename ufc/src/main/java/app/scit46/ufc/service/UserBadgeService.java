package app.scit46.ufc.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.UserBadgeDTO;
import app.scit46.ufc.entity.UserBadgeEntity;
import app.scit46.ufc.repository.UserBadgeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserBadgeService {
    private final UserBadgeRepository userBadgeRepository;

    public List<UserBadgeDTO> getUserBadge(Long userId) {
        List<UserBadgeEntity> userBadges = userBadgeRepository.findByUser_UserId(userId);
        return userBadges.stream()
                .map(UserBadgeDTO::toDTO)
                .collect(Collectors.toList());
    }
}
