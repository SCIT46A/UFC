package app.scit46.ufc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CreatorApprovalDTO {

    @JsonProperty("b_no")
    private String bRegistNumber; // 사업자 등록번호

    @JsonProperty("p_nm")
    private String bName; // 대표자명

    @JsonProperty("start_dt")
    private String startDt; // 개업일자

    @JsonProperty("p_nm2")
    @Builder.Default
    private String pNm2 = "";

    @JsonProperty("b_nm")
    @Builder.Default
    private String bNm = "";

    @JsonProperty("corp_no")
    @Builder.Default
    private String corpNo = "";

    @JsonProperty("b_sector")
    @Builder.Default
    private String bSector = "";

    @JsonProperty("b_type")
    @Builder.Default
    private String bType = "";

    @JsonProperty("b_adr")
    @Builder.Default
    private String bAdr = "";

    // API 요청에서 제외
    @JsonIgnore
    private String businessValidationStatus;

    @JsonIgnore
    private Boolean approvalAvailable;
}
