package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.EnseigneDetailsDTO;
import tn.fst.spring.backend_pfs_s2.dto.EnseigneDTO;
import tn.fst.spring.backend_pfs_s2.model.Enseigne;
import tn.fst.spring.backend_pfs_s2.model.Semestre;
import tn.fst.spring.backend_pfs_s2.model.TypeMatiere;
import tn.fst.spring.backend_pfs_s2.service.CustomUserDetails;
import tn.fst.spring.backend_pfs_s2.service.EnseigneService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enseigne")
@Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"})
public class EnseigneController {

    private final EnseigneService enseigneService;

    public EnseigneController(EnseigneService enseigneService) {
        this.enseigneService = enseigneService;
    }

    @GetMapping
    @Secured("ROLE_ADMIN")
    public List<EnseigneDetailsDTO> getAllEnseignes() {
        return enseigneService.getAllEnseignes().stream()
                .map(this::convertToDetailsDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/my")
    @Secured("ROLE_ENSEIGNANT")
    public List<EnseigneDetailsDTO> getMyEnseignes(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long enseignantId = userDetails.getUserId();
        return enseigneService.getEnseignesByEnseignantId(enseignantId).stream()
                .map(this::convertToDetailsDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public EnseigneDetailsDTO getEnseigneById(@PathVariable Long id) {
        return convertToDetailsDTO(enseigneService.getEnseigneById(id));
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public EnseigneDetailsDTO createEnseigne(@RequestBody EnseigneDTO enseigneDTO) {
        Enseigne created = enseigneService.createEnseigne(convertToEntity(enseigneDTO));
        return convertToDetailsDTO(created);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public EnseigneDetailsDTO updateEnseigne(@PathVariable Long id, @RequestBody EnseigneDTO enseigneDTO) {
        Enseigne updated = enseigneService.updateEnseigne(id, convertToEntity(enseigneDTO));
        return convertToDetailsDTO(updated);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public void deleteEnseigne(@PathVariable Long id) {
        enseigneService.deleteEnseigne(id);
    }

    private EnseigneDTO convertToDTO(Enseigne enseigne) {
        EnseigneDTO dto = new EnseigneDTO();
        dto.setId(enseigne.getId());
        dto.setEnseignantId(enseigne.getEnseignant().getId());
        dto.setMatiereId(enseigne.getMatiere().getId());
        dto.setNumSemestre(enseigne.getNumSemestre().name());
        dto.setAnneeUniversitaireId(enseigne.getAnnee().getId());
        dto.setTypeMatiere(TypeMatiere.valueOf(enseigne.getTypeMatiere().name()));
        return dto;
    }

    private EnseigneDetailsDTO convertToDetailsDTO(Enseigne enseigne) {
        if (enseigne == null) return null;

        EnseigneDetailsDTO dto = new EnseigneDetailsDTO();
        dto.setId(enseigne.getId());

        // Info Enseignant
        if (enseigne.getEnseignant() != null) {
            dto.setEnseignantId(enseigne.getEnseignant().getId());
            dto.setEnseignantNom(enseigne.getEnseignant().getNom());
            dto.setEnseignantPrenom(enseigne.getEnseignant().getPrenom());
            dto.setEnseignantGrade(enseigne.getEnseignant().getGrade());
        }

        // Info Matière
        if (enseigne.getMatiere() != null) {
            dto.setMatiereId(enseigne.getMatiere().getId());
            dto.setMatiereNom(enseigne.getMatiere().getNom());
            dto.setMatiereCode(enseigne.getMatiere().getCode());
        }

        // Info Année Universitaire
        if (enseigne.getAnnee() != null) {
            dto.setAnneeId(enseigne.getAnnee().getId());
            dto.setAnneeDateDebut(enseigne.getAnnee().getDateDebut());
            dto.setAnneeDateFin(enseigne.getAnnee().getDateFin());
        }

        dto.setNumSemestre(enseigne.getNumSemestre() != null ? enseigne.getNumSemestre().name() : null);
        dto.setTypeMatiere(enseigne.getTypeMatiere() != null ? enseigne.getTypeMatiere().name() : null);

        return dto;
    }

    private Enseigne convertToEntity(EnseigneDTO dto) {
        Enseigne enseigne = new Enseigne();
        enseigne.setId(dto.getId());
        if (dto.getNumSemestre() != null) {
            enseigne.setNumSemestre(Semestre.valueOf(dto.getNumSemestre()));
        }
        if (dto.getTypeMatiere() != null) {
            enseigne.setTypeMatiere(TypeMatiere.valueOf(String.valueOf(dto.getTypeMatiere())));
        }
        return enseigne;
    }
}