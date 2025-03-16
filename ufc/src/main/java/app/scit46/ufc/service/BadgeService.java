package app.scit46.ufc.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.BadgeDTO;
import app.scit46.ufc.entity.BadgeEntity;
import app.scit46.ufc.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeRepository badgeRepository;

    public List<BadgeDTO> getBadges() {
        List<BadgeEntity> badges = badgeRepository.findAll();
        return badges.stream()
                .map(BadgeDTO::toDTO)
                .collect(Collectors.toList());
    }
}