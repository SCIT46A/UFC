package app.scit46.ufc.dto.reward;

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
    private Long donationId;
    private String invoice; // "택배사코드#송장번호" 형태
}
