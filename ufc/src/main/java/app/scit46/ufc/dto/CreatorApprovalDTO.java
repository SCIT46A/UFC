package app.scit46.ufc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CreatorApprovalDTO {

    @JsonProperty("b_no")  // API 요구사항에 맞게 변경
    private String bRegistNumber;  // 사업자 등록번호

    @JsonProperty("p_nm")  // API 요구사항에 맞게 변경
    private String bName;  // 대표자명

    @JsonProperty("start_dt")  // API 요구사항에 맞게 변경
    private String startDt ;  // 개업일자

    @JsonProperty("p_nm2")
    private String pNm2 = "";  // 빈 문자열 기본값

    @JsonProperty("b_nm")
    private String bNm = "";  // 빈 문자열 기본값

    @JsonProperty("corp_no")
    private String corpNo = "";  // 빈 문자열 기본값

    @JsonProperty("b_sector")
    private String bSector = "";  // 빈 문자열 기본값

    @JsonProperty("b_type")
    private String bType = "";  // 빈 문자열 기본값

    @JsonProperty("b_adr")
    private String bAdr = "";  // 빈 문자열 기본값

    // API 요청에는 포함되지 않도록 @JsonIgnore 추가
    @JsonIgnore
    private String businessValidationStatus;

    @JsonIgnore
    private Boolean approvalAvailable;
}
