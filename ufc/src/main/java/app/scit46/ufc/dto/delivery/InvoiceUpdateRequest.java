package app.scit46.ufc.dto.delivery;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.lang.Long;
import java.lang.String;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceUpdateRequest {
    private Long id;
    private String invoice; // "택배사코드#송장번호" 형태
    private String courier; // "택배사코드"
    private String trackingNumber; // "송장번호"

    // ✅ invoice만 필요한 경우
    public InvoiceUpdateRequest(Long id, String invoice) {
        this.id = id;
        this.invoice = invoice;
    }

    // ✅ 개별 courier와 trackingNumber를 사용하는 경우
    public InvoiceUpdateRequest(Long id, String courier, String trackingNumber) {
        this.id = id;
        this.courier = courier;
        this.trackingNumber = trackingNumber;
        this.invoice = courier + "#" + trackingNumber; // 자동 생성 가능
    }
}
