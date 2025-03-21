package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.EnseigneDTO;
import tn.fst.spring.backend_pfs_s2.model.Enseigne;
import tn.fst.spring.backend_pfs_s2.model.Semestre;
import tn.fst.spring.backend_pfs_s2.service.EnseigneService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enseignes")
public class EnseigneController {

    @Autowired
    private EnseigneService enseigneService;

    @GetMapping
    public List<EnseigneDTO> getAllEnseignes() {
        return enseigneService.getAllEnseignes().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EnseigneDTO getEnseigneById(@PathVariable Long id) {
        Enseigne enseigne = enseigneService.getEnseigneById(id);
        return convertToDTO(enseigne);
    }

    @PostMapping
    public EnseigneDTO createEnseigne(@RequestBody EnseigneDTO enseigneDTO) {
        Enseigne enseigne = convertToEntity(enseigneDTO);
        Enseigne createdEnseigne = enseigneService.createEnseigne(enseigne);
        return convertToDTO(createdEnseigne);
    }

    @PutMapping("/{id}")
    public EnseigneDTO updateEnseigne(@PathVariable Long id, @RequestBody EnseigneDTO enseigneDTO) {
        Enseigne enseigne = convertToEntity(enseigneDTO);
        Enseigne updatedEnseigne = enseigneService.updateEnseigne(id, enseigne);
        return convertToDTO(updatedEnseigne);
    }

    @DeleteMapping("/{id}")
    public void deleteEnseigne(@PathVariable Long id) {
        enseigneService.deleteEnseigne(id);
    }

    private EnseigneDTO convertToDTO(Enseigne enseigne) {
        EnseigneDTO dto = new EnseigneDTO();
        dto.setId(enseigne.getId());
        dto.setEnseignantId(enseigne.getEnseignant().getId());
        dto.setMatiereId(enseigne.getMatiere().getId());
        dto.setNumSemestre(String.valueOf(enseigne.getNumSemestre()));
        dto.setAnneeUniversitaireId(enseigne.getAnnee().getId());
        dto.setTypeMatiere(enseigne.getTypeMatiere());
        return dto;
    }

    private Enseigne convertToEntity(EnseigneDTO dto) {
        Enseigne enseigne = new Enseigne();
        enseigne.setId(dto.getId());
        // Vous devez récupérer les entités associées par leurs IDs ici
        enseigne.setNumSemestre(Semestre.valueOf(dto.getNumSemestre()));
        enseigne.setTypeMatiere(dto.getTypeMatiere());
        return enseigne;
    }
}