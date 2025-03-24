package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.DisponibiliteEnseignantDTO;
import tn.fst.spring.backend_pfs_s2.model.DisponibiliteEnseignant;
import tn.fst.spring.backend_pfs_s2.service.DisponibiliteEnseignantService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/disponibilites")
public class DisponibiliteEnseignantController {

    @Autowired
    private DisponibiliteEnseignantService disponibiliteService;

    @GetMapping("/enseignant/{enseignantId}")
    public List<DisponibiliteEnseignantDTO> getDisponibilitesByEnseignant(@PathVariable Long enseignantId) {
        return disponibiliteService.getDisponibilitesByEnseignant(enseignantId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/surveillance/{surveillanceId}")
    public List<DisponibiliteEnseignantDTO> getDisponibilitesBySurveillance(@PathVariable Long surveillanceId) {
        return disponibiliteService.getDisponibilitesBySurveillance(surveillanceId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
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
        }

        if (disponibilite.getSurveillance() != null) {
            dto.setSurveillanceId(disponibilite.getSurveillance().getId());
        }

        return dto;
    }
}