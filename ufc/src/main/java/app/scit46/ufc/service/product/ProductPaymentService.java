package app.scit46.ufc.service.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.product.ProductPaymentDTO;
import app.scit46.ufc.entity.product.ProductPaymentEntity;
import app.scit46.ufc.exception.handler.DBExceptionHandler;
import app.scit46.ufc.repository.product.ProductPaymentRepository;
import app.scit46.ufc.service.CourierService;
import app.scit46.ufc.service.delivery.DeliveryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductPaymentService {

    private final ProductPaymentRepository productPaymentRepository;
    private final ProductDeliveryService productDeliveryService;
    private final DeliveryService deliveryService;
    private final CourierService courierService;
    private static final Logger logger = LoggerFactory.getLogger(ProductPaymentService.class);

    // ✅ 크리에이터의 주문 내역 조회
    public Map<String, Object> getOrdersByCreator(Long creatorId) {
        List<Map<String, Object>> rawOrders = productPaymentRepository.findOrdersByCreator(creatorId);

        // ✅ 1. 새로운 수정 가능한 리스트 생성 (TupleBackedMap -> HashMap)
        List<Map<String, Object>> mutableOrders = rawOrders.stream().map(order -> {
            Map<String, Object> orderCopy = new HashMap<>(order); // 새 HashMap에 복사

            // ✅ 송장번호(invoice)에서 택배사 ID와 송장번호 분리
            String invoice = (String) order.get("invoice");
            String courierId = null;
            String trackingNumber = null;

            if (invoice != null && invoice.contains("#")) {
                String[] invoiceParts = invoice.split("#", 2);
                courierId = invoiceParts[0];
                trackingNumber = invoiceParts[1];
            }

            // ✅ 새로운 HashMap에 값 추가 (이제 변경 가능)
            orderCopy.put("courierId", courierId);
            orderCopy.put("trackingNumber", trackingNumber);

            return orderCopy;
        }).collect(Collectors.toList());

        // ✅ 2. 송장이 있는 주문만 필터링하여 배송 상태 조회
        List<Map<String, Object>> ordersWithInvoice = mutableOrders.stream()
                .filter(order -> order.get("trackingNumber") != null) // 이제 trackingNumber가 세팅됨
                .collect(Collectors.toList());

        // ✅ 3. 비동기 배송 상태 조회 및 업데이트 (한 번에 처리)
        Map<String, String> updatedDeliveryStatuses = deliveryService.trackProductDelivery(ordersWithInvoice);

        // ✅ 4. 모든 주문 데이터를 변환 (송장이 없는 주문도 포함)
        List<Map<String, Object>> transformedOrders = mutableOrders.stream().map(order -> {
            Map<String, Object> orderMap = new HashMap<>(order); // 다시 변경 가능한 HashMap 생성

            // ✅ 디버깅 로그 추가
            logger.info("🛒 주문 데이터 매핑: payId={}, courierId={}, trackingNumber={}",
                    order.get("payId"), order.get("courierId"), order.get("trackingNumber"));

            // ✅ 주문 데이터 매핑
            orderMap.put("courierName", courierService.getCourierNameById((String) order.get("courierId")));

            // ✅ 5. 배송 상태 업데이트 (송장이 있는 경우만 최신값 반영)
            String updatedStatus = updatedDeliveryStatuses.get(order.get("payId").toString());
            orderMap.put("deliveryStatus", updatedStatus != null ? updatedStatus : "미등록");

            return orderMap;
        }).collect(Collectors.toList());

        // ✅ 6. 주문 상태별 개수 계산
        Map<String, Long> orderCounts = mutableOrders.stream()
                .collect(Collectors.groupingBy(order -> mapPaymentStatus((String) order.get("paymentStatus")),
                        Collectors.counting()));

        return Map.of(
                "orders", transformedOrders,
                "orderCounts", orderCounts);
    }

    @Transactional
    public void processOrder(Long payId) {
        ProductPaymentEntity order = productPaymentRepository.findById(payId)
                .orElseThrow(() -> new IllegalArgumentException("🚨 주문 정보를 찾을 수 없음: " + payId));

        if (!"completed".equals(order.getStatus())) {
            throw new IllegalStateException("🚨 결제 완료된 주문만 발주할 수 있습니다.");
        }

        order.setStatus("ordered"); // ✅ 발주 완료 상태 변경
        productPaymentRepository.save(order);
    }

    @Transactional
    public void approveCancel(Long payId) {
        ProductPaymentEntity payment = productPaymentRepository.findById(payId)
                .orElseThrow(() -> new IllegalArgumentException("🚨 주문을 찾을 수 없음: " + payId));

        if (!"pending".equals(payment.getStatus())) {
            throw new IllegalStateException("🚨 이 주문은 취소 요청 상태가 아닙니다.");
        }

        // ✅ 주문 상태를 'cancelled'로 변경
        payment.setStatus("cancelled");
        productPaymentRepository.save(payment);
    }

    private String mapPaymentStatus(String status) {
        return switch (status) {
            case "completed" -> "결제 완료";
            case "pending" -> "취소 요청";
            case "cancelled" -> "취소됨";
            case "ordered" -> "발주 완료";
            default -> "알 수 없음";
        };
    }

    public List<ProductPaymentDTO> getProductPaymentsByUserId(Long userId) {
        return productPaymentRepository.findByPurchasedByUserId(userId)
                .stream()
                .map(ProductPaymentDTO::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long payId) {
        Optional<ProductPaymentEntity> temp = productPaymentRepository.findById(payId);
        if (!temp.isPresent())
            return;
        ProductPaymentEntity entity = temp.get();
        entity.setStatus("pending");
        productPaymentRepository.save(entity);
    }

    public ProductPaymentDTO findByPayId(Long payId) {
        return productPaymentRepository.findById(payId)
                .map(ProductPaymentDTO::toDTO)
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다."));
    }

}
