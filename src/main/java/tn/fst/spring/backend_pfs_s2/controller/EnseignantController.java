package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.EnseignantDTO;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import tn.fst.spring.backend_pfs_s2.service.EnseignantService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enseignants")
public class EnseignantController {

    @Autowired
    private EnseignantService enseignantService;

    @GetMapping
    public List<EnseignantDTO> getAllEnseignants() {
        return enseignantService.getAllEnseignants().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EnseignantDTO getEnseignantById(@PathVariable Long id) {
        Enseignant enseignant = enseignantService.getEnseignantById(id);
        return convertToDTO(enseignant);
    }

    @PostMapping
    public EnseignantDTO createEnseignant(@RequestBody EnseignantDTO enseignantDTO) {
        Enseignant enseignant = convertToEntity(enseignantDTO);
        Enseignant createdEnseignant = enseignantService.createEnseignant(enseignant);
        return convertToDTO(createdEnseignant);
    }

    @PutMapping("/{id}")
    public EnseignantDTO updateEnseignant(@PathVariable Long id, @RequestBody EnseignantDTO enseignantDTO) {
        Enseignant enseignant = convertToEntity(enseignantDTO);
        Enseignant updatedEnseignant = enseignantService.updateEnseignant(id, enseignant);
        return convertToDTO(updatedEnseignant);
    }

    @DeleteMapping("/{id}")
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
        dto.setEstDisponible(enseignant.getEstDisponible());
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
        enseignant.setEstDisponible(dto.getEstDisponible());
        return enseignant;
    }
}
