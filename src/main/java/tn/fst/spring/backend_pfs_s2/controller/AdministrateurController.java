package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.AdministrateurDTO;
import tn.fst.spring.backend_pfs_s2.model.Administrateur;
import tn.fst.spring.backend_pfs_s2.service.AdministrateurService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/administrateurs")
public class AdministrateurController {

    @Autowired
    private AdministrateurService administrateurService;

    @GetMapping
    public List<AdministrateurDTO> getAllAdministrateurs() {
        return administrateurService.getAllAdministrateurs().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AdministrateurDTO getAdministrateurById(@PathVariable Long id) {
        Administrateur administrateur = administrateurService.getAdministrateurById(id);
        return convertToDTO(administrateur);
    }

    @PostMapping
    public AdministrateurDTO createAdministrateur(@RequestBody AdministrateurDTO administrateurDTO) {
        Administrateur administrateur = convertToEntity(administrateurDTO);
        Administrateur createdAdministrateur = administrateurService.createAdministrateur(administrateur);
        return convertToDTO(createdAdministrateur);
    }

    @PutMapping("/{id}")
    public AdministrateurDTO updateAdministrateur(@PathVariable Long id, @RequestBody AdministrateurDTO administrateurDTO) {
        Administrateur administrateur = convertToEntity(administrateurDTO);
        Administrateur updatedAdministrateur = administrateurService.updateAdministrateur(id, administrateur);
        return convertToDTO(updatedAdministrateur);
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
        Administrateur administrateur = new Administrateur();
        administrateur.setId(dto.getId());
        administrateur.setNom(dto.getNom());
        administrateur.setPrenom(dto.getPrenom());
        administrateur.setEmail(dto.getEmail());
        administrateur.setTelephone(dto.getTelephone());
        administrateur.setFonction(dto.getFonction());
        return administrateur;
    }
}