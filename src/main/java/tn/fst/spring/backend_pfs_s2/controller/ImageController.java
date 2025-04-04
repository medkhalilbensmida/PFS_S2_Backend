package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @GetMapping("/default/{type}/{filename:.+}")
    public ResponseEntity<Resource> getDefaultImage(
            @PathVariable String type,
            @PathVariable String filename) {
        try {
            Path file = Paths.get("src/main/resources/static/assets/images/" + type + "/" + filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(file))
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/profile/{filename:.+}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String filename) {
        try {
            // 1. Check in uploads first
            Path uploadedFile = Paths.get(uploadDir + "/assets/images/profile/" + filename);
            if (Files.exists(uploadedFile)) {
                Resource resource = new UrlResource(uploadedFile.toUri());
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            }

            // 2. If not found in uploads, check in default resources
            Path defaultFile = Paths.get("src/main/resources/static/assets/images/profile/" + filename);
            Resource defaultResource = new UrlResource(defaultFile.toUri());

            if (defaultResource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(defaultResource);
            }

            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/default-profiles")
    public ResponseEntity<List<String>> getDefaultProfileImages() {
        List<String> defaultImages = Arrays.asList(
                "male.jpg",
                "user.jpg",
                "female.jpg"
        );
        return ResponseEntity.ok(defaultImages);
    }

    @GetMapping("/default-profiles/{filename:.+}")
    public ResponseEntity<Resource> getDefaultProfileImage(@PathVariable String filename) {
        try {
            Path file = Paths.get("src/main/resources/static/assets/images/profile").resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(file))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    private ResponseEntity<Resource> serveImage(String folder, String filename) {
        try {
            Path file = Paths.get(uploadDir).resolve("assets/images/" + folder + "/" + filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(file))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/signatures/{filename:.+}")
    public ResponseEntity<Resource> getSignatureImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("./uploads/assets/images/signatures").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}