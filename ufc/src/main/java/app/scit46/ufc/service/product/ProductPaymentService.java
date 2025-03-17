package app.scit46.ufc.service.product;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.product.ProductPaymentDTO;
import app.scit46.ufc.entity.product.ProductPaymentEntity;
import app.scit46.ufc.repository.ProductPaymentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductPaymentService {

    private final ProductPaymentRepository productPaymentRepository;

    public List<ProductPaymentDTO> getProductPaymentsByPurchasedByUserId(Long userId) {
        List<ProductPaymentEntity> productPayments = productPaymentRepository.findByPurchasedByUserId(userId);
        return productPayments.stream()
            .map(ProductPaymentDTO::toDTO)
            .collect(Collectors.toList());
    }


}
