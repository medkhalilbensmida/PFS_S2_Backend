package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.AdministrateurDTO;
import tn.fst.spring.backend_pfs_s2.model.Administrateur;
import tn.fst.spring.backend_pfs_s2.service.AdministrateurService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/administrateurs")
@Secured("ROLE_ADMIN")
public class AdministrateurController {

    private final AdministrateurService administrateurService;

    public AdministrateurController(AdministrateurService administrateurService) {
        this.administrateurService = administrateurService;
    }

    @GetMapping
    public List<AdministrateurDTO> getAllAdministrateurs() {
        return administrateurService.getAllAdministrateurs().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AdministrateurDTO getAdministrateurById(@PathVariable Long id) {
        return convertToDTO(administrateurService.getAdministrateurById(id));
    }

    @PostMapping
    public AdministrateurDTO createAdministrateur(@RequestBody AdministrateurDTO administrateurDTO) {
        Administrateur created = administrateurService.createAdministrateur(convertToEntity(administrateurDTO));
        return convertToDTO(created);
    }

    @PutMapping("/{id}")
    public AdministrateurDTO updateAdministrateur(@PathVariable Long id, @RequestBody AdministrateurDTO administrateurDTO) {
        Administrateur updated = administrateurService.updateAdministrateur(id, convertToEntity(administrateurDTO));
        return convertToDTO(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteAdministrateur(@PathVariable Long id) {
        administrateurService.deleteAdministrateur(id);
    }

    private AdministrateurDTO convertToDTO(Administrateur administrateur) {
        AdministrateurDTO dto = new AdministrateurDTO();
        dto.setId(administrateur.getId());
        dto.setNom(administrateur.getNom());
        dto.setPrenom(administrateur.getPrenom());
        dto.setEmail(administrateur.getEmail());
        dto.setTelephone(administrateur.getTelephone());
        dto.setFonction(administrateur.getFonction());
        return dto;
    }

    private Administrateur convertToEntity(AdministrateurDTO dto) {
        Administrateur admin = new Administrateur();
        admin.setId(dto.getId());
        admin.setNom(dto.getNom());
        admin.setPrenom(dto.getPrenom());
        admin.setEmail(dto.getEmail());
        admin.setTelephone(dto.getTelephone());
        admin.setFonction(dto.getFonction());
        return admin;
    }
}