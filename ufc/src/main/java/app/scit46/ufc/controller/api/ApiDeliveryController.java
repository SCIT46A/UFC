package app.scit46.ufc.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;

import app.scit46.ufc.service.delivery.DeliveryService;

@RestController
@RequestMapping("api/delivery")
public class ApiDeliveryController {

    private final DeliveryService deliveryService;
    private static final Logger logger = LoggerFactory.getLogger(ApiDeliveryController.class);

    public ApiDeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/{invoice}")
    public ResponseEntity<Map<String, Object>> track(@PathVariable("invoice") String invoice) {
        try {
            String[] parts = invoice.split("#");
            if (parts.length != 2) {
                return ResponseEntity.badRequest().body(Map.of("error", "잘못된 송장 번호 형식"));
            }

            String courierName = parts[0];
            String trackingNumber = parts[1];

            logger.info("🚚 컨트롤러에서 받은 송장번호: {}", invoice);

            String trackingResult = deliveryService.trackDelivery(courierName, trackingNumber);

            Map<String, Object> response = new HashMap<>();
            response.put("trackingData", trackingResult);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("🚨 배송조회 중 오류 발생:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "서버 오류 발생"));
        }
    }
}
