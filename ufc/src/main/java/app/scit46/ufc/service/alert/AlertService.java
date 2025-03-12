package app.scit46.ufc.service.alert;

import org.springframework.stereotype.Service;

import app.scit46.ufc.repository.alert.AlertRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;

}
