package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.SalleDTO;
import tn.fst.spring.backend_pfs_s2.model.Salle;
import tn.fst.spring.backend_pfs_s2.service.SalleService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/salles")
public class SalleController {

    @Autowired
    private SalleService salleService;

    @GetMapping
    public List<SalleDTO> getAllSalles() {
        return salleService.getAllSalles().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SalleDTO getSalleById(@PathVariable Long id) {
        Salle salle = salleService.getSalleById(id);
        return convertToDTO(salle);
    }

    @PostMapping
    public SalleDTO createSalle(@RequestBody SalleDTO salleDTO) {
        Salle salle = convertToEntity(salleDTO);
        Salle createdSalle = salleService.createSalle(salle);
        return convertToDTO(createdSalle);
    }

    @PutMapping("/{id}")
    public SalleDTO updateSalle(@PathVariable Long id, @RequestBody SalleDTO salleDTO) {
        Salle salle = convertToEntity(salleDTO);
        Salle updatedSalle = salleService.updateSalle(id, salle);
        return convertToDTO(updatedSalle);
    }

    @DeleteMapping("/{id}")
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