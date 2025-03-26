package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.*;
import tn.fst.spring.backend_pfs_s2.model.Matiere;
import tn.fst.spring.backend_pfs_s2.service.MatiereService;
import tn.fst.spring.backend_pfs_s2.service.CustomUserDetails;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matieres")
@Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"})
public class MatiereController {

    private final MatiereService matiereService;

    public MatiereController(MatiereService matiereService) {
        this.matiereService = matiereService;
    }

    @GetMapping("/All")
    @Secured("ROLE_ADMIN")
    public List<MatiereDTO> getAllMatieres() {
        return matiereService.getAllMatieres().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/my")
    @Secured("ROLE_ENSEIGNANT")
    public List<EnseignantMatiereDTO> getMyMatieres() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long enseignantId = userDetails.getUserId();

        return matiereService.getMatieresDetailsByEnseignantId(enseignantId);
    }
    @GetMapping("/{id}")
    public MatiereDTO getMatiereById(@PathVariable Long id) {
        return convertToDTO(matiereService.getMatiereById(id));
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public MatiereDTO createMatiere(@RequestBody MatiereDTO matiereDTO) {
        Matiere created = matiereService.createMatiere(convertToEntity(matiereDTO));
        return convertToDTO(created);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public MatiereDTO updateMatiere(@PathVariable Long id, @RequestBody MatiereDTO matiereDTO) {
        Matiere updated = matiereService.updateMatiere(id, convertToEntity(matiereDTO));
        return convertToDTO(updated);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
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