package app.scit46.ufc.service;

import org.springframework.stereotype.Service;

import app.scit46.ufc.entity.reward.RewardEntity;
import app.scit46.ufc.repository.RewardRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RewardService {
    private final RewardRepository rewardRepository;

    public RewardEntity addReward(RewardEntity reward) {
        return rewardRepository.save(reward);
    }
}
