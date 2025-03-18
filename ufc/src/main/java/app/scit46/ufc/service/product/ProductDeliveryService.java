package app.scit46.ufc.service.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import app.scit46.ufc.repository.product.ProductDeliveryRepository;
import app.scit46.ufc.repository.product.ProductPaymentRepository;
import app.scit46.ufc.repository.ProductRepository;
import app.scit46.ufc.entity.product.ProductDeliveryEntity;
import app.scit46.ufc.dto.delivery.InvoiceUpdateRequest;
import app.scit46.ufc.dto.product.ProductDeliveryDTO;

import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductDeliveryService {

    private final ProductDeliveryRepository productDeliveryRepository;
    private final ProductPaymentRepository productPaymentRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void updateInvoice(Long payId, Long productId, String courier, String trackingNumber) {
        Optional<ProductDeliveryEntity> existingDelivery = productDeliveryRepository.findPDeliveryIdByPay_PayId(payId);

        if (existingDelivery.isPresent()) {
            // ✅ 기존 배송 정보가 있으면 업데이트
            ProductDeliveryEntity delivery = existingDelivery.get();
            delivery.setInvoice(courier + "#" + trackingNumber);
            delivery.setStatus("preparing");
            productDeliveryRepository.save(delivery);
            System.out.println("✅ 기존 배송 정보 업데이트 완료: " + payId);
        } else {
            // ✅ 기존 배송 정보가 없으면 새로 생성
            ProductDeliveryEntity newDelivery = new ProductDeliveryEntity();
            newDelivery.setPay(productPaymentRepository.findById(payId)
                    .orElseThrow(() -> new IllegalArgumentException("🚨 결제 정보를 찾을 수 없음: " + payId)));
            newDelivery.setProduct(productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("🚨 상품을 찾을 수 없음: " + productId)));
            newDelivery.setInvoice(courier + "#" + trackingNumber);
            newDelivery.setStatus("preparing"); // 초기 상태
            productDeliveryRepository.save(newDelivery);
            System.out.println("✅ 새로운 배송 정보 생성: " + payId);
        }
    }

    @Transactional
    public void updateInvoices(List<InvoiceUpdateRequest> updateRequests) {
        for (InvoiceUpdateRequest request : updateRequests) {
            Optional<ProductDeliveryEntity> existingDelivery = productDeliveryRepository
                    .findPDeliveryIdByPay_PayId(request.getId());

            if (existingDelivery.isPresent()) {
                System.out.println("🚨 기존 배송 정보가 존재하여 업데이트하지 않음: " + request.getId());
            } else {
                // ✅ 기존 배송 정보가 없을 경우, payId 기반으로 productId 조회
                Long productId = productPaymentRepository.findProductIdByPayId(request.getId())
                        .orElseThrow(() -> new IllegalArgumentException("🚨 productId를 찾을 수 없음: " + request.getId()));

                ProductDeliveryEntity newDelivery = new ProductDeliveryEntity();
                newDelivery.setPay(productPaymentRepository.findById(request.getId())
                        .orElseThrow(() -> new IllegalArgumentException("🚨 결제 정보를 찾을 수 없음: " + request.getId())));
                newDelivery.setProduct(productRepository.findById(productId)
                        .orElseThrow(() -> new IllegalArgumentException("🚨 상품을 찾을 수 없음: " + productId)));
                newDelivery.setInvoice(request.getCourier() + "#" + request.getTrackingNumber());
                newDelivery.setStatus("preparing");
                productDeliveryRepository.save(newDelivery);
                System.out.println("✅ 새로운 배송 정보 생성: " + request.getId());
            }
        }
    }

    public ProductDeliveryDTO getProductDeliveryByPayId(Long payId) {
        ProductDeliveryEntity deliveryEntity = productDeliveryRepository.findProductDeliveryByPayId(payId);
        return deliveryEntity != null ? ProductDeliveryDTO.toDTO(deliveryEntity) : null;
    }

}
