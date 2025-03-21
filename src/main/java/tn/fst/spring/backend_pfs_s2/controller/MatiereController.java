package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.MatiereDTO;
import tn.fst.spring.backend_pfs_s2.model.Matiere;
import tn.fst.spring.backend_pfs_s2.service.MatiereService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matieres")
public class MatiereController {

    @Autowired
    private MatiereService matiereService;

    @GetMapping
    public List<MatiereDTO> getAllMatieres() {
        return matiereService.getAllMatieres().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MatiereDTO getMatiereById(@PathVariable Long id) {
        Matiere matiere = matiereService.getMatiereById(id);
        return convertToDTO(matiere);
    }

    @PostMapping
    public MatiereDTO createMatiere(@RequestBody MatiereDTO matiereDTO) {
        Matiere matiere = convertToEntity(matiereDTO);
        Matiere createdMatiere = matiereService.createMatiere(matiere);
        return convertToDTO(createdMatiere);
    }

    @PutMapping("/{id}")
    public MatiereDTO updateMatiere(@PathVariable Long id, @RequestBody MatiereDTO matiereDTO) {
        Matiere matiere = convertToEntity(matiereDTO);
        Matiere updatedMatiere = matiereService.updateMatiere(id, matiere);
        return convertToDTO(updatedMatiere);
    }

    @DeleteMapping("/{id}")
    public void deleteMatiere(@PathVariable Long id) {
        matiereService.deleteMatiere(id);
    }

    private MatiereDTO convertToDTO(Matiere matiere) {
        MatiereDTO dto = new MatiereDTO();
        dto.setId(matiere.getId());
        dto.setNiveau(matiere.getNiveau());
        dto.setSection(matiere.getSection());
        dto.setCode(matiere.getCode());
        dto.setNom(matiere.getNom());
        return dto;
    }

    private Matiere convertToEntity(MatiereDTO dto) {
        Matiere matiere = new Matiere();
        matiere.setId(dto.getId());
        matiere.setNiveau(dto.getNiveau());
        matiere.setSection(dto.getSection());
        matiere.setCode(dto.getCode());
        matiere.setNom(dto.getNom());
        return matiere;
    }
}