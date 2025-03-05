package app.scit46.ufc.dto.cloudflare;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// https://developers.cloudflare.com/api/resources/images/subresources/v1/methods/get/#(params)%20default%20%3E%20(param)%20image_id%20%3E%20(schema)
public class ApiResponse {
    // Cloudflare API 응답 정보 형식
    private List<ResponseInfo> errors;
    private List<ResponseInfo> messages;
    private Image result;
    private boolean success;

    // 내부 클래스 정의
    @Getter
    @Setter
    public static class ResponseInfo {
        private int code;
        private String message;
    }
}
