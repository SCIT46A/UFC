package app.scit46.ufc.dto.custom;

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
    private String intro;
    // private String businessCert; // ✅ PhotoDTO 포함
    private String registNumber;
    private String bizName;
    private String companyName;
    private String address;
    private String backImg; // ✅ PhotoDTO 포함
    private String profileImg;
}
