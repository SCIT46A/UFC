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
import org.springframework.web.bind.annotation.RequestParam;

import app.scit46.ufc.service.delivery.DeliveryService;

@RestController
@RequestMapping("api/delivery")
public class ApiDeliveryController {

    private final DeliveryService deliveryService;
    private static final Logger logger = LoggerFactory.getLogger(ApiDeliveryController.class);

    public ApiDeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getDeliveryStatus(
            @RequestParam("courierId") String courierId,
            @RequestParam("trackingNumber") String trackingNumber) {

        String status = deliveryService.trackDelivery(courierId, trackingNumber);

        // ✅ JSON 응답 형식으로 변경
        Map<String, String> response = new HashMap<>();
        response.put("status", status);

        return ResponseEntity.ok(response); // ✅ JSON 반환
    }

}
