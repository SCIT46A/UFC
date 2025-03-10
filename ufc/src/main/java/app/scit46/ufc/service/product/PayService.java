package app.scit46.ufc.service.product;


import kr.co.bootpay.Bootpay;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class PayService {

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

    public void gopay(String receiptId) { // receiptId를 매개변수로 받음
        goGetToken(); // 토큰 발급 후 검증 진행
        try {
            HashMap res = getBootpay().confirm(receiptId);
            if (res.get("error_code") == null) { // 검증 성공
                System.out.println("결제 검증 성공: " + res);
            } else {
                System.out.println("결제 검증 실패: " + res.get("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
