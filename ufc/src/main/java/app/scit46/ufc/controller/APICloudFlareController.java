package app.scit46.ufc.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import app.scit46.ufc.dto.cloudflare.ApiResponse;
import app.scit46.ufc.service.cloudflare.ImageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cloudflare")
public class APICloudFlareController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String response = imageService.uploadImage(file);   //Image Id
        return ResponseEntity.ok(response);
    }
}
