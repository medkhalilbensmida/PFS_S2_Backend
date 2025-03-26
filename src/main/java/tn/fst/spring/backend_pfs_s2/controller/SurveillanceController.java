package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.SurveillanceDTO;
import tn.fst.spring.backend_pfs_s2.model.Surveillance;
import tn.fst.spring.backend_pfs_s2.service.SurveillanceService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/surveillances")
@Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"})
public class SurveillanceController {

    private final SurveillanceService surveillanceService;

    public SurveillanceController(SurveillanceService surveillanceService) {
        this.surveillanceService = surveillanceService;
    }

    @GetMapping
    public List<SurveillanceDTO> getAllSurveillances() {
        return surveillanceService.getAllSurveillances().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SurveillanceDTO getSurveillanceById(@PathVariable Long id) {
        return convertToDTO(surveillanceService.getSurveillanceById(id));
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public SurveillanceDTO createSurveillance(@RequestBody SurveillanceDTO surveillanceDTO) {
        Surveillance created = surveillanceService.createSurveillance(convertToEntity(surveillanceDTO));
        return convertToDTO(created);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public SurveillanceDTO updateSurveillance(@PathVariable Long id, @RequestBody SurveillanceDTO surveillanceDTO) {
        Surveillance updated = surveillanceService.updateSurveillance(id, convertToEntity(surveillanceDTO));
        return convertToDTO(updated);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public void deleteSurveillance(@PathVariable Long id) {
        surveillanceService.deleteSurveillance(id);
    }

    private SurveillanceDTO convertToDTO(Surveillance surveillance) {
        SurveillanceDTO dto = new SurveillanceDTO();
        dto.setId(surveillance.getId());
        dto.setDateDebut(surveillance.getDateDebut());
        dto.setDateFin(surveillance.getDateFin());
        dto.setStatut(surveillance.getStatut());
        if (surveillance.getSalle() != null) {
            dto.setSalleId(surveillance.getSalle().getId());
        }
        if (surveillance.getMatiere() != null) {
            dto.setMatiereId(surveillance.getMatiere().getId());
        }
        if (surveillance.getEnseignantPrincipal() != null) {
            dto.setEnseignantPrincipalId(surveillance.getEnseignantPrincipal().getId());
        }
        if (surveillance.getEnseignantSecondaire() != null) {
            dto.setEnseignantSecondaireId(surveillance.getEnseignantSecondaire().getId());
        }
        if (surveillance.getSessionExamen() != null) {
            dto.setSessionExamenId(surveillance.getSessionExamen().getId());
        }
        return dto;
    }

    private Surveillance convertToEntity(SurveillanceDTO dto) {
        Surveillance surveillance = new Surveillance();
        surveillance.setId(dto.getId());
        surveillance.setDateDebut(dto.getDateDebut());
        surveillance.setDateFin(dto.getDateFin());
        surveillance.setStatut(dto.getStatut());
        return surveillance;
    }
}