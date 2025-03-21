package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.AnneeUniversitaireDTO;
import tn.fst.spring.backend_pfs_s2.model.AnneeUniversitaire;
import tn.fst.spring.backend_pfs_s2.service.AnneeUniversitaireService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/annees")
public class AnneeUniversitaireController {

    @Autowired
    private AnneeUniversitaireService anneeUniversitaireService;

    @GetMapping
    public List<AnneeUniversitaireDTO> getAllAnnees() {
        return anneeUniversitaireService.getAllAnnees().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AnneeUniversitaireDTO getAnneeById(@PathVariable Long id) {
        AnneeUniversitaire annee = anneeUniversitaireService.getAnneeById(id);
        return convertToDTO(annee);
    }

    @PostMapping
    public AnneeUniversitaireDTO createAnnee(@RequestBody AnneeUniversitaireDTO anneeDTO) {
        AnneeUniversitaire annee = convertToEntity(anneeDTO);
        AnneeUniversitaire createdAnnee = anneeUniversitaireService.createAnnee(annee);
        return convertToDTO(createdAnnee);
    }

    @PutMapping("/{id}")
    public AnneeUniversitaireDTO updateAnnee(@PathVariable Long id, @RequestBody AnneeUniversitaireDTO anneeDTO) {
        AnneeUniversitaire annee = convertToEntity(anneeDTO);
        AnneeUniversitaire updatedAnnee = anneeUniversitaireService.updateAnnee(id, annee);
        return convertToDTO(updatedAnnee);
    }

    @DeleteMapping("/{id}")
    public void deleteAnnee(@PathVariable Long id) {
        anneeUniversitaireService.deleteAnnee(id);
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