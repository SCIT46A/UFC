package app.scit46.ufc.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/cloudflare")
public class APICloudFlareController {

    @Value("${cloudflare.account-id}")
    private String accountId;

    @Value("${cloudflare.api-token}")
    private String apiToken;

    @GetMapping("/credentials")
    public ResponseEntity<Map<String, String>> getCredentials(HttpSession session) {
        // 사용자 인증 상태 확인
        if (!isUserAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, String> credentials = new HashMap<>();
        credentials.put("accountId", accountId);
        credentials.put("apiToken", apiToken);

        return ResponseEntity.ok(credentials);
    }

    private boolean isUserAuthenticated(HttpSession session) {
        // 세션을 통한 사용자 인증 확인 로직
        return session.getAttribute("user") != null;
    }
}
