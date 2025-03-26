package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.EnseignantDTO;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import tn.fst.spring.backend_pfs_s2.service.EnseignantService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enseignants")
@Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"})
public class EnseignantController {

    private final EnseignantService enseignantService;

    public EnseignantController(EnseignantService enseignantService) {
        this.enseignantService = enseignantService;
    }

    @GetMapping
    public List<EnseignantDTO> getAllEnseignants() {
        return enseignantService.getAllEnseignants().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
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
    public EnseignantDTO updateEnseignant(@PathVariable Long id, @RequestBody EnseignantDTO enseignantDTO) {
        Enseignant updated = enseignantService.updateEnseignant(id, convertToEntity(enseignantDTO));
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
        return enseignant;
    }
}