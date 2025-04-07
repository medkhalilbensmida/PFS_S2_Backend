package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.ChangePasswordRequest;
import tn.fst.spring.backend_pfs_s2.dto.EnseignantDTO;
import tn.fst.spring.backend_pfs_s2.dto.ErrorResponse;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import tn.fst.spring.backend_pfs_s2.repository.EnseignantRepository;
import tn.fst.spring.backend_pfs_s2.service.EnseignantService;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enseignants")
@Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"})
public class EnseignantController {

    private final EnseignantService enseignantService;
    private final EnseignantRepository enseignantRepository;
    private final PasswordEncoder passwordEncoder;

    public EnseignantController(EnseignantService enseignantService,
                                EnseignantRepository enseignantRepository,
                                PasswordEncoder passwordEncoder) {
        this.enseignantService = enseignantService;
        this.enseignantRepository = enseignantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<EnseignantDTO> getAllEnseignants() {
        return enseignantService.getAllEnseignants().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/change-password")
    @Secured({"ROLE_ENSEIGNANT", "ROLE_ADMIN"})
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request,
                                            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("AUTH_REQUIRED", "Authentification requise"));
            }

            String email = authentication.getName();
            boolean isAuthorized = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority ->
                            grantedAuthority.getAuthority().equals("ROLE_ENSEIGNANT") ||
                                    grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            if (!isAuthorized) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("ACCESS_DENIED", "Vous n'avez pas les droits nécessaires"));
            }

            Enseignant enseignant = enseignantRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            if (!passwordEncoder.matches(request.getCurrentPassword(), enseignant.getMotDePasse())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("PASSWORD_MISMATCH", "Le mot de passe actuel est incorrect"));
            }

            if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("INVALID_PASSWORD", "Le nouveau mot de passe ne peut pas être vide"));
            }

            enseignant.setMotDePasse(passwordEncoder.encode(request.getNewPassword()));
            enseignantRepository.save(enseignant);

            return ResponseEntity.ok().build();

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("SERVER_ERROR", "Erreur lors du changement de mot de passe: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"})
    public EnseignantDTO getEnseignantById(@PathVariable Long id) {
        return convertToDTO(enseignantService.getEnseignantById(id));
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public EnseignantDTO createEnseignant(@RequestBody EnseignantDTO enseignantDTO) {
        Enseignant created = enseignantService.createEnseignant(convertToEntity(enseignantDTO));
        return convertToDTO(created);
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"})
    public EnseignantDTO updateEnseignant(@PathVariable Long id,
                                          @RequestBody EnseignantDTO enseignantDTO) {
        Enseignant existing = enseignantService.getEnseignantById(id);

        existing.setNom(enseignantDTO.getNom());
        existing.setPrenom(enseignantDTO.getPrenom());
        existing.setEmail(enseignantDTO.getEmail());
        existing.setTelephone(enseignantDTO.getTelephone());
        existing.setGrade(enseignantDTO.getGrade());
        existing.setDepartement(enseignantDTO.getDepartement());
        existing.setPhotoProfil(enseignantDTO.getPhotoProfil());

        if (enseignantDTO.getMotDePasse() != null && !enseignantDTO.getMotDePasse().isEmpty()) {
            existing.setMotDePasse(passwordEncoder.encode(enseignantDTO.getMotDePasse()));
        }

        Enseignant updated = enseignantService.updateEnseignant(id, existing);
        return convertToDTO(updated);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public void deleteEnseignant(@PathVariable Long id) {
        enseignantService.deleteEnseignant(id);
    }

    private EnseignantDTO convertToDTO(Enseignant enseignant) {
        EnseignantDTO dto = new EnseignantDTO();
        dto.setId(enseignant.getId());
        dto.setNom(enseignant.getNom());
        dto.setPrenom(enseignant.getPrenom());
        dto.setEmail(enseignant.getEmail());
        dto.setTelephone(enseignant.getTelephone());
        dto.setGrade(enseignant.getGrade());
        dto.setDepartement(enseignant.getDepartement());
        dto.setPhotoProfil(enseignant.getPhotoProfil());
        return dto;
    }

    private Enseignant convertToEntity(EnseignantDTO dto) {
        Enseignant enseignant = new Enseignant();
        enseignant.setId(dto.getId());
        enseignant.setNom(dto.getNom());
        enseignant.setPrenom(dto.getPrenom());
        enseignant.setEmail(dto.getEmail());
        enseignant.setTelephone(dto.getTelephone());
        enseignant.setGrade(dto.getGrade());
        enseignant.setDepartement(dto.getDepartement());
        enseignant.setPhotoProfil(dto.getPhotoProfil());
        return enseignant;
    }
}