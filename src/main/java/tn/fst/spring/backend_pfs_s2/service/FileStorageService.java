package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String storeProfileImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir + "/assets/images/profile");
        return storeImage(file, uploadPath, "profile_");
    }

    public String storeSignatureImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir + "/assets/images/signatures");
        return storeImage(file, uploadPath, "signature_");
    }

    private String storeImage(MultipartFile file, Path uploadPath, String prefix) throws IOException {
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = prefix + UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}