package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.SurveillanceDTO;
import tn.fst.spring.backend_pfs_s2.model.Surveillance;
import tn.fst.spring.backend_pfs_s2.service.SurveillanceService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/surveillances")
public class SurveillanceController {

    @Autowired
    private SurveillanceService surveillanceService;

    @GetMapping
    public List<SurveillanceDTO> getAllSurveillances() {
        return surveillanceService.getAllSurveillances().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SurveillanceDTO getSurveillanceById(@PathVariable Long id) {
        Surveillance surveillance = surveillanceService.getSurveillanceById(id);
        return convertToDTO(surveillance);
    }

    @PostMapping
    public SurveillanceDTO createSurveillance(@RequestBody SurveillanceDTO surveillanceDTO) {
        Surveillance surveillance = convertToEntity(surveillanceDTO);
        Surveillance createdSurveillance = surveillanceService.createSurveillance(surveillance);
        return convertToDTO(createdSurveillance);
    }

    @PutMapping("/{id}")
    public SurveillanceDTO updateSurveillance(@PathVariable Long id, @RequestBody SurveillanceDTO surveillanceDTO) {
        Surveillance surveillance = convertToEntity(surveillanceDTO);
        Surveillance updatedSurveillance = surveillanceService.updateSurveillance(id, surveillance);
        return convertToDTO(updatedSurveillance);
    }

    @DeleteMapping("/{id}")
    public void deleteSurveillance(@PathVariable Long id) {
        surveillanceService.deleteSurveillance(id);
    }

    private SurveillanceDTO convertToDTO(Surveillance surveillance) {
        SurveillanceDTO dto = new SurveillanceDTO();
        dto.setId(surveillance.getId());
        dto.setDateDebut(surveillance.getDateDebut());
        dto.setDateFin(surveillance.getDateFin());
        dto.setStatut(surveillance.getStatut());
        dto.setSalleId(surveillance.getSalle().getId());
        dto.setMatiereId(surveillance.getMatiere().getId());
        dto.setEnseignantPrincipalId(surveillance.getEnseignantPrincipal().getId());
        dto.setEnseignantSecondaireId(surveillance.getEnseignantSecondaire().getId());
        dto.setSessionExamenId(surveillance.getSessionExamen().getId());
        return dto;
    }

    private Surveillance convertToEntity(SurveillanceDTO dto) {
        Surveillance surveillance = new Surveillance();
        surveillance.setId(dto.getId());
        surveillance.setDateDebut(dto.getDateDebut());
        surveillance.setDateFin(dto.getDateFin());
        surveillance.setStatut(dto.getStatut());
        // Vous devez récupérer les entités associées par leurs IDs ici
        return surveillance;
    }
}
