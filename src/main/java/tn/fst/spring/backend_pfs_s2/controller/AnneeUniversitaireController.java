package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.AnneeUniversitaireDTO;
import tn.fst.spring.backend_pfs_s2.model.AnneeUniversitaire;
import tn.fst.spring.backend_pfs_s2.service.AnneeUniversitaireService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/annees")
@Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"})
public class AnneeUniversitaireController {

    private final AnneeUniversitaireService anneeService;

    public AnneeUniversitaireController(AnneeUniversitaireService anneeService) {
        this.anneeService = anneeService;
    }

    @GetMapping
    public List<AnneeUniversitaireDTO> getAllAnnees() {
        return anneeService.getAllAnnees().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AnneeUniversitaireDTO getAnneeById(@PathVariable Long id) {
        return convertToDTO(anneeService.getAnneeById(id));
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public AnneeUniversitaireDTO createAnnee(@RequestBody AnneeUniversitaireDTO anneeDTO) {
        AnneeUniversitaire created = anneeService.createAnnee(convertToEntity(anneeDTO));
        return convertToDTO(created);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public AnneeUniversitaireDTO updateAnnee(@PathVariable Long id, @RequestBody AnneeUniversitaireDTO anneeDTO) {
        AnneeUniversitaire updated = anneeService.updateAnnee(id, convertToEntity(anneeDTO));
        return convertToDTO(updated);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public void deleteAnnee(@PathVariable Long id) {
        anneeService.deleteAnnee(id);
    }

    private AnneeUniversitaireDTO convertToDTO(AnneeUniversitaire annee) {
        AnneeUniversitaireDTO dto = new AnneeUniversitaireDTO();
        dto.setId(annee.getId());
        dto.setDateDebut(annee.getDateDebut());
        dto.setDateFin(annee.getDateFin());
        dto.setEstActive(annee.getEstActive());
        return dto;
    }

    private AnneeUniversitaire convertToEntity(AnneeUniversitaireDTO dto) {
        AnneeUniversitaire annee = new AnneeUniversitaire();
        annee.setId(dto.getId());
        annee.setDateDebut(dto.getDateDebut());
        annee.setDateFin(dto.getDateFin());
        annee.setEstActive(dto.getEstActive());
        return annee;
    }
}