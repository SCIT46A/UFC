package app.scit46.ufc.service.product;

import app.scit46.ufc.dto.UserDTO;
import app.scit46.ufc.dto.product.ProductDTO;
import app.scit46.ufc.dto.product.ProductPaymentDTO;
import app.scit46.ufc.entity.product.ProductEntity;
import app.scit46.ufc.entity.product.ProductPaymentEntity;
import app.scit46.ufc.repository.ProductRepository;
import app.scit46.ufc.repository.product.ProductPaymentRepository;
import app.scit46.ufc.service.UserService;
import kr.co.bootpay.Bootpay;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.bootpay.model.request.Cancel;
import app.scit46.ufc.service.product.ProductPaymentService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PayService {

    private final ProductService productService;

    private final UserService userService;

    private final ProductPaymentRepository productPaymentRepository;

    private final ProductRepository productRepository;

    private final ProductPaymentService productPaymentService;

    @Value("${bootpay.RestAPI-key}")
    private String restAPIKey;

    @Value("${bootpay.Private-Key}")
    private String privateKey;

    @Value("${bootpay.Application-Key}")
    private String applicationKey;

    // 필드 초기화 대신, Bootpay 객체를 지연 초기화합니다.
    private Bootpay bootpay;

    private Bootpay getBootpay() {
        if (bootpay == null) {
            bootpay = new Bootpay(restAPIKey, privateKey);
            // Bootpay 라이브러리에서 applicationId(또는 Application-Key)를 설정하는 메서드가 있다면 사용하세요.
            // 예: bootpay.setApplicationId(applicationKey);
        }
        return bootpay;
    }

    private void goGetToken() {
        try {
            HashMap res = getBootpay().getAccessToken();
            if (res.get("error_code") == null) { // 토큰 발급 성공
                String token = (String) res.get("access_token");
                getBootpay().setToken(token); // 발급받은 토큰을 Bootpay 객체에 설정
                System.out.println("토큰 발급 성공: " + token);
            } else {
                System.out.println("토큰 발급 실패: " + res.get("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 여기 트렌젹센 걸고 이제 성공 시 작업 하면됩니다~
    @Transactional
    public void gopay(Map<String, Object> payload) {
        String receiptId = (String) payload.get("receipt_id");

        goGetToken(); // 토큰 발급 후 검증 진행

        try {
            HashMap res = getBootpay().confirm(receiptId);

            // 🔍 부트페이 응답 로그
            System.out.println("🔍 부트페이 응답: " + res);

            // ✅ 부트페이 결제 검증 성공 여부 체크
            if (res.get("status") == null || !"1".equals(res.get("status").toString())) {
                throw new RuntimeException("❌ 결제 검증 실패: " + res.get("message"));
            }

            // ✅ 정상적으로 검증이 완료되었으면, 결제 정보 저장 진행
            Long productId = Long.parseLong(payload.get("product_id").toString());
            ProductDTO productDTO = productService.findProductById(productId);

            Integer totalPay = Integer.parseInt(payload.get("total_pay").toString());
            Byte stock = Byte.parseByte(payload.get("stock").toString());
            Long userId = Long.parseLong(payload.get("user_id").toString());
            UserDTO userDTO = userService.findByIdDTO(userId);

            ProductPaymentDTO productPaymentDTO = new ProductPaymentDTO();
            productPaymentDTO.setProduct(productDTO);
            productPaymentDTO.setPrice(totalPay);
            productPaymentDTO.setStock(stock);
            productPaymentDTO.setPurchasedBy(userDTO);
            productPaymentDTO.setReceiptNumber(receiptId);
            productPaymentDTO.setIsAdjust(false);
            productPaymentDTO.setStatus("completed");

            ProductPaymentEntity target = ProductPaymentEntity.toEntity(productPaymentDTO);
            productPaymentRepository.save(target);

            // ✅ 기존 ProductEntity를 DB에서 조회
            ProductEntity productEntity = productRepository.findById(productDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("상품 정보를 찾을 수 없습니다."));

            // ✅ 기존 ProductEntity의 stockQuantity 값을 수정
            productEntity.setStockQuantity(productEntity.getStockQuantity() - stock);

            // ✅ 수정된 엔티티 저장
            productRepository.save(productEntity);

            System.out.println("✅ 결제 검증 성공: " + res);
        } catch (Exception e) {
            throw new RuntimeException("결제 검증 과정에서 오류 발생", e);
        }
    }

    @Transactional
    public void cancel(Long payId) {

        Bootpay api = getBootpay();
        goGetToken(); // 토큰 발급 후 검증 진행

        ProductPaymentDTO productPaymentDTO = productPaymentService.findByPayId(payId);

        String receiptId = productPaymentDTO.getReceiptNumber();

        Cancel cancel = new Cancel();
        cancel.receiptId = receiptId;
        cancel.cancelUsername = "test";
        cancel.cancelMessage = "test";

        try {
            api.receiptCancel(cancel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
