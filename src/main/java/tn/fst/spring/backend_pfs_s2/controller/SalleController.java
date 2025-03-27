package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.SalleDTO;
import tn.fst.spring.backend_pfs_s2.model.Salle;
import tn.fst.spring.backend_pfs_s2.service.SalleService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/salles")
@Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"})
public class SalleController {

    private final SalleService salleService;

    public SalleController(SalleService salleService) {
        this.salleService = salleService;
    }

    @GetMapping
    public List<SalleDTO> getAllSalles() {
        return salleService.getAllSalles().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public SalleDTO getSalleById(@PathVariable Long id) {
        return convertToDTO(salleService.getSalleById(id));
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public SalleDTO createSalle(@RequestBody SalleDTO salleDTO) {
        Salle created = salleService.createSalle(convertToEntity(salleDTO));
        return convertToDTO(created);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public SalleDTO updateSalle(@PathVariable Long id, @RequestBody SalleDTO salleDTO) {
        Salle updated = salleService.updateSalle(id, convertToEntity(salleDTO));
        return convertToDTO(updated);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public void deleteSalle(@PathVariable Long id) {
        salleService.deleteSalle(id);
    }

    private SalleDTO convertToDTO(Salle salle) {
        SalleDTO dto = new SalleDTO();
        dto.setId(salle.getId());
        dto.setNumero(salle.getNumero());
        dto.setCapacite(salle.getCapacite());
        dto.setBatiment(salle.getBatiment());
        dto.setEtage(salle.getEtage());
        return dto;
    }

    private Salle convertToEntity(SalleDTO dto) {
        Salle salle = new Salle();
        salle.setId(dto.getId());
        salle.setNumero(dto.getNumero());
        salle.setCapacite(dto.getCapacite());
        salle.setBatiment(dto.getBatiment());
        salle.setEtage(dto.getEtage());
        return salle;
    }
}