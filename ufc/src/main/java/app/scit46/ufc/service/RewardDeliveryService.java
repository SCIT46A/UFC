package app.scit46.ufc.service;

import app.scit46.ufc.repository.RewardDeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RewardDeliveryService {

    private final RewardDeliveryRepository rewardDeliveryRepository;
}
