package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.AdministrateurDTO;
import tn.fst.spring.backend_pfs_s2.dto.ChangePasswordRequest;
import tn.fst.spring.backend_pfs_s2.dto.ErrorResponse;
import tn.fst.spring.backend_pfs_s2.model.Administrateur;
import tn.fst.spring.backend_pfs_s2.repository.AdministrateurRepository;
import tn.fst.spring.backend_pfs_s2.service.AdministrateurService;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/administrateurs")
@Secured("ROLE_ADMIN")
public class AdministrateurController {

    private final AdministrateurService administrateurService;
    private final AdministrateurRepository administrateurRepository;
    private final PasswordEncoder passwordEncoder;

    public AdministrateurController(AdministrateurService administrateurService,
                                    AdministrateurRepository administrateurRepository,
                                    PasswordEncoder passwordEncoder) {
        this.administrateurService = administrateurService;
        this.administrateurRepository = administrateurRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping
    public List<AdministrateurDTO> getAllAdministrateurs() {
        return administrateurService.getAllAdministrateurs().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AdministrateurDTO getAdministrateurById(@PathVariable Long id) {
        return convertToDTO(administrateurService.getAdministrateurById(id));
    }

    @PostMapping
    public AdministrateurDTO createAdministrateur(@RequestBody AdministrateurDTO administrateurDTO) {
        Administrateur created = administrateurService.createAdministrateur(convertToEntity(administrateurDTO));
        return convertToDTO(created);
    }

    @PutMapping("/{id}")
    public AdministrateurDTO updateAdministrateur(@PathVariable Long id,
                                                  @RequestBody AdministrateurDTO administrateurDTO) {
        // Récupérer l'admin existant
        Administrateur existing = administrateurService.getAdministrateurById(id);

        // Mettre à jour uniquement les champs modifiables
        existing.setNom(administrateurDTO.getNom());
        existing.setPrenom(administrateurDTO.getPrenom());
        existing.setEmail(administrateurDTO.getEmail());
        existing.setTelephone(administrateurDTO.getTelephone());
        existing.setFonction(administrateurDTO.getFonction());

        // Ne pas mettre à jour le mot de passe s'il est null ou vide
        if (administrateurDTO.getMotDePasse() != null && !administrateurDTO.getMotDePasse().isEmpty()) {
            existing.setMotDePasse(passwordEncoder.encode(administrateurDTO.getMotDePasse()));
        }

        Administrateur updated = administrateurService.updateAdministrateur(id, existing);
        return convertToDTO(updated);
    }

    @PostMapping("/change-password")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request,
                                            Authentication authentication) {
        try {
            // 1. Vérification de base de l'authentification
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("AUTH_REQUIRED", "Authentification requise"));
            }

            String email = authentication.getName();

            // 2. Recherche de l'administrateur avec gestion d'erreur spécifique
            Administrateur admin = administrateurRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Administrateur avec email " + email + " non trouvé"));

            // 3. Vérification du mot de passe actuel
            if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getMotDePasse())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("PASSWORD_MISMATCH", "Le mot de passe actuel est incorrect"));
            }

            // 4. Validation du nouveau mot de passe
            if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("INVALID_PASSWORD", "Le nouveau mot de passe ne peut pas être vide"));
            }

            if (request.getNewPassword().length() < 6) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("PASSWORD_TOO_SHORT", "Le mot de passe doit contenir au moins 6 caractères"));
            }

            // 5. Mise à jour du mot de passe
            admin.setMotDePasse(passwordEncoder.encode(request.getNewPassword()));
            administrateurRepository.save(admin);

            return ResponseEntity.ok().build();

        } catch (RuntimeException e) {
            // 6. Gestion d'erreur plus spécifique
            if (e.getMessage().contains("non trouvé")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("USER_NOT_FOUND", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("SERVER_ERROR", "Erreur technique lors du changement de mot de passe"));
        }
    }

    @DeleteMapping("/{id}")
    public void deleteAdministrateur(@PathVariable Long id) {
        administrateurService.deleteAdministrateur(id);
    }

    private AdministrateurDTO convertToDTO(Administrateur administrateur) {
        AdministrateurDTO dto = new AdministrateurDTO();
        dto.setId(administrateur.getId());
        dto.setNom(administrateur.getNom());
        dto.setPrenom(administrateur.getPrenom());
        dto.setEmail(administrateur.getEmail());
        dto.setTelephone(administrateur.getTelephone());
        dto.setFonction(administrateur.getFonction());
        // Ne pas renvoyer le mot de passe dans le DTO
        return dto;
    }

    private Administrateur convertToEntity(AdministrateurDTO dto) {
        Administrateur admin = new Administrateur();
        admin.setId(dto.getId());
        admin.setNom(dto.getNom());
        admin.setPrenom(dto.getPrenom());
        admin.setEmail(dto.getEmail());
        admin.setTelephone(dto.getTelephone());
        admin.setFonction(dto.getFonction());
        // Ne pas modifier le mot de passe ici
        return admin;
    }
}