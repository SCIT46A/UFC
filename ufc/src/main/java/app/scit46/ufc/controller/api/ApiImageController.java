package app.scit46.ufc.controller.api;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import app.scit46.ufc.service.ImageUrlService;
import app.scit46.ufc.service.UserService;
import app.scit46.ufc.service.cloudflare.ImageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ApiImageController {

    // cloudflare 이미지 제어 서비스
    private final ImageService imageService;

    // 유저 서비스
    private final UserService userService;

    // 이미지 DB/URL 제어 서비스
    private final ImageUrlService imageUrlService;

    // 이미지 파일을 cloudflare에 업로드 후 이미지ID 반환
    // UserDetails 검토 필요
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails) {
        // 세션에 접속한 유저 이름을 통해 유저 ID 조회
        Long userId = userService.findUserIdByUserName(userDetails.getUsername());
        // 파일과 업로드한 유저의 ID를 파라미터로 전달하여 이미지 업로드/DB 저장 후 이미지ID 반환
        String result = imageService.uploadImage(file, userId);   //Image Id
        // 이미지 ID를 통해 이미지 URL 반환
        String imageUrl = ImageService.getImageUrl(result);
        return ResponseEntity.ok(imageUrl);
    }

    // 이미지 ID를 통해 이미지 URL 반환
    @GetMapping("/{imageId}")
    public ResponseEntity<String> getImageUrl(@PathVariable String imageId) {
        String result = ImageService.getImageUrl(imageId);
        return ResponseEntity.ok(result);
    }

    // 이미지 ID를 통해 이미지 삭제
    // UserDetails 검토 필요
    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<Boolean> deleteImage(@PathVariable String imageId, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findUserIdByUserName(userDetails.getUsername());
        // 이미지 삭제 권한 검사
        if(userId != imageUrlService.getUploadedBy(imageId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
        }
        boolean result = imageService.deleteImage(imageId);
        return ResponseEntity.ok(result);
    }
}
