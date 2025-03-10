package app.scit46.ufc.dto.custom;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class CreatorCreateDTO {
    private Long id;
    private String intro;
    // private String businessCert; // ✅ PhotoDTO 포함
    private String registNumber;
    private String bizName;
    private String companyName;
    private String address;
    private String backImg; // ✅ PhotoDTO 포함
    private String profileImg;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bRegistDate; // 추가된 필드
}
