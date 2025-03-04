package app.scit46.ufc.service;

import app.scit46.ufc.dto.CourierDTO;
import app.scit46.ufc.repository.CourierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourierService {

    private final CourierRepository courierRepository;


    public List<CourierDTO> findAll() {
        return courierRepository.findAll().stream().map(CourierDTO::toDTO).collect(Collectors.toList());
    }




}
