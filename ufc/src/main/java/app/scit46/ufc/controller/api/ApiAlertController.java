package app.scit46.ufc.controller.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.scit46.ufc.dto.alert.UserAlertDTO;
import app.scit46.ufc.service.UserService;
import app.scit46.ufc.service.alert.UserAlertService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alert")
public class ApiAlertController {
    
    private final UserAlertService userAlertService;
    private final UserService userService;
    // 사용자 인증정보 기반으로 해당 사용자에 대한 모든 알림을 조회
    @GetMapping("/list")
    public ResponseEntity<List<UserAlertDTO>> getAlertList(HttpServletRequest request) {

        String oauthId = request.getUserPrincipal().getName();
        Long userId = userService.findUserByIdentity(oauthId).getUserId();

        // 사용자 아이디를 기반으로 해당 사용자에 대한 모든 알림을 조회
        List<UserAlertDTO> alertList = new ArrayList<>();
        userAlertService.getUserAlertList(userId).forEach(alert -> alertList.add(alert));

        return ResponseEntity.ok(alertList);
    }

    @GetMapping("/read")
    public ResponseEntity<Boolean> readAlertProcess(@RequestParam(name="linkId") Long linkId){
      return ResponseEntity.ok(userAlertService.readAlert(linkId));
    }

    // @GetMapping("/read")
    // public ResponseEntity<Boolean> readAlertProcess(@RequestBody Map<String, Long> data){
    //   Long userAlertId = data.get("linkId");
    //   return ResponseEntity.ok(userAlertService.readAlert(userAlertId));
    // }
}
