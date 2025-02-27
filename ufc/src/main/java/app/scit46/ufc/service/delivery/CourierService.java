package app.scit46.ufc.service.delivery;

import org.springframework.stereotype.Service;

import app.scit46.ufc.repository.delivery.CourierRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourierService {

    private final CourierRepository courierRepository;

    public String getCourierNameById(String courierId) {
        return courierRepository.findCourierNameByCourierId(courierId);
    }
}
