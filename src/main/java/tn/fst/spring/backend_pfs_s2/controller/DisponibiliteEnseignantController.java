package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.DisponibiliteEnseignantDTO;
import tn.fst.spring.backend_pfs_s2.model.DisponibiliteEnseignant;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import tn.fst.spring.backend_pfs_s2.service.CustomUserDetails;
import tn.fst.spring.backend_pfs_s2.service.DisponibiliteEnseignantService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/disponibilites")
public class DisponibiliteEnseignantController {

    private final DisponibiliteEnseignantService disponibiliteService;

    public DisponibiliteEnseignantController(DisponibiliteEnseignantService disponibiliteService) {
        this.disponibiliteService = disponibiliteService;
    }

    // Endpoint pour l'enseignant connecté
    @GetMapping("/my-disponibilities")
    @Secured("ROLE_ENSEIGNANT")
    public ResponseEntity<?> getMyDisponibilities(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            Long enseignantId = customUserDetails.getUserId();

            List<DisponibiliteEnseignantDTO> disponibilites = disponibiliteService
                    .getDisponibilitesByEnseignant(enseignantId)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(disponibilites);
        } catch (ClassCastException e) {
            return ResponseEntity.status(500).body("Erreur de conversion des détails utilisateur");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la récupération des disponibilités");
        }
    }

    // Endpoint pour l'admin (toutes les disponibilités)
    @GetMapping
    @Secured("ROLE_ADMIN")
    public List<DisponibiliteEnseignantDTO> getAllDisponibilities() {
        return disponibiliteService.getAllDisponibilites()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/surveillance/{surveillanceId}")
    @Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"})
    public List<DisponibiliteEnseignantDTO> getDisponibilitesBySurveillance(@PathVariable Long surveillanceId) {
        return disponibiliteService.getDisponibilitesBySurveillance(surveillanceId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ENSEIGNANT")
    public DisponibiliteEnseignantDTO updateDisponibilite(@PathVariable Long id, @RequestParam Boolean estDisponible) {
        DisponibiliteEnseignant updated = disponibiliteService.updateDisponibilite(id, estDisponible);
        return convertToDTO(updated);
    }

    private DisponibiliteEnseignantDTO convertToDTO(DisponibiliteEnseignant disponibilite) {
        DisponibiliteEnseignantDTO dto = new DisponibiliteEnseignantDTO();
        dto.setId(disponibilite.getId());
        dto.setEstDisponible(disponibilite.getEstDisponible());
        if (disponibilite.getEnseignant() != null) {
            dto.setEnseignantId(disponibilite.getEnseignant().getId());
            dto.setEnseignantNom(disponibilite.getEnseignant().getNom());
            dto.setEnseignantPrenom(disponibilite.getEnseignant().getPrenom());
        }
        if (disponibilite.getSurveillance() != null) {
            dto.setSurveillanceId(disponibilite.getSurveillance().getId());
        }
        return dto;
    }
}