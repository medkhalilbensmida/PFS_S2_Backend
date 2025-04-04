package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.fst.spring.backend_pfs_s2.model.Administrateur;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import tn.fst.spring.backend_pfs_s2.repository.AdministrateurRepository;
import tn.fst.spring.backend_pfs_s2.repository.EnseignantRepository;
import tn.fst.spring.backend_pfs_s2.service.FileStorageService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileStorageService fileStorageService;
    private final EnseignantRepository enseignantRepository;
    private final AdministrateurRepository administrateurRepository;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif"
    );

    public FileUploadController(FileStorageService fileStorageService,
                                EnseignantRepository enseignantRepository,
                                AdministrateurRepository administrateurRepository) {
        this.fileStorageService = fileStorageService;
        this.enseignantRepository = enseignantRepository;
        this.administrateurRepository = administrateurRepository;
    }

    @PostMapping("/upload-profile")
    @Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"})
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam("userType") String userType) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Veuillez sélectionner un fichier");
        }

        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            return ResponseEntity.badRequest().body("Type de fichier non supporté");
        }

        try {
            String fileName = fileStorageService.storeProfileImage(file);
            String filePath = "/api/images/profile/" + fileName;

            if ("ENSEIGNANT".equalsIgnoreCase(userType)) {
                Enseignant enseignant = enseignantRepository.findById(userId).orElse(null);
                if (enseignant != null) {
                    enseignant.setPhotoProfil(filePath);
                    enseignantRepository.save(enseignant);
                    return ResponseEntity.ok(Map.of("filePath", filePath));
                }
            } else if ("ADMIN".equalsIgnoreCase(userType)) {
                Administrateur admin = administrateurRepository.findById(userId).orElse(null);
                if (admin != null) {
                    admin.setPhotoProfil(filePath);
                    administrateurRepository.save(admin);
                    return ResponseEntity.ok(Map.of("filePath", filePath));
                }
            }

            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erreur lors du téléchargement du fichier");
        }
    }

    @PostMapping("/upload-signature")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> uploadSignature(
            @RequestParam("file") MultipartFile file,
            @RequestParam("adminId") Long adminId) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Veuillez sélectionner un fichier");
        }

        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            return ResponseEntity.badRequest().body("Type de fichier non supporté");
        }

        try {
            String fileName = fileStorageService.storeSignatureImage(file);
            String filePath = "/api/images/signatures/" + fileName; // Modifié pour utiliser /api/images/

            Administrateur admin = administrateurRepository.findById(adminId).orElse(null);
            if (admin != null) {
                admin.setSignature(filePath);
                administrateurRepository.save(admin);
                return ResponseEntity.ok(Map.of("filePath", filePath));
            }

            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erreur lors du téléchargement de la signature");
        }
    }


    @GetMapping("/default-profiles")
    public ResponseEntity<List<String>> getDefaultProfileImages() {
        List<String> defaultImages = Arrays.asList(
                "user-1.jpg",
                "user-2.jpg",
                "user-3.jpg",
                "user-4.jpg",
                "user-5.jpg"
        );
        return ResponseEntity.ok(defaultImages);
    }

    @PostMapping("/select-default-profile")
    @Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"})
    public ResponseEntity<?> selectDefaultProfile(@RequestBody Map<String, Object> request) {
        try {
            String imageName = (String) request.get("imageName");
            Long userId = Long.valueOf(request.get("userId").toString());
            String userType = (String) request.get("userType");

            // Validate it's one of the allowed default images
            List<String> allowedImages = Arrays.asList(
                    "male.jpg",
                    "user.jpg",
                    "female.jpg"
            );

            if (!allowedImages.contains(imageName)) {
                return ResponseEntity.badRequest().body("Image non autorisée");
            }

            String filePath = "/api/images/profile/" + imageName;

            if ("ENSEIGNANT".equalsIgnoreCase(userType)) {
                Enseignant enseignant = enseignantRepository.findById(userId).orElse(null);
                if (enseignant != null) {
                    enseignant.setPhotoProfil(filePath);
                    enseignantRepository.save(enseignant);
                    return ResponseEntity.ok(Map.of("filePath", filePath));
                }
            } else if ("ADMIN".equalsIgnoreCase(userType)) {
                Administrateur admin = administrateurRepository.findById(userId).orElse(null);
                if (admin != null) {
                    admin.setPhotoProfil(filePath);
                    administrateurRepository.save(admin);
                    return ResponseEntity.ok(Map.of("filePath", filePath));
                }
            }

            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Requête invalide");
        }
    }

}