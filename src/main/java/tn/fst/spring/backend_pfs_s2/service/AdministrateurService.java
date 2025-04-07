package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.model.Administrateur;
import tn.fst.spring.backend_pfs_s2.repository.AdministrateurRepository;

import java.util.List;

@Service
public class AdministrateurService {

    @Autowired
    private AdministrateurRepository administrateurRepository;

    public List<Administrateur> getAllAdministrateurs() {
        return administrateurRepository.findAll();
    }

    public Administrateur getAdministrateurById(Long id) {
        return administrateurRepository.findById(id).orElse(null);
    }

    public Administrateur createAdministrateur(Administrateur administrateur) {
        return administrateurRepository.save(administrateur);
    }

    public Administrateur updateAdministrateur(Long id, Administrateur administrateur) {
        return administrateurRepository.findById(id)
                .map(existingAdmin -> {
                    // Mettre à jour uniquement les champs nécessaires
                    if (administrateur.getNom() != null) {
                        existingAdmin.setNom(administrateur.getNom());
                    }
                    if (administrateur.getPrenom() != null) {
                        existingAdmin.setPrenom(administrateur.getPrenom());
                    }
                    if (administrateur.getEmail() != null) {
                        existingAdmin.setEmail(administrateur.getEmail());
                    }
                    if (administrateur.getTelephone() != null) {
                        existingAdmin.setTelephone(administrateur.getTelephone());
                    }
                    if (administrateur.getFonction() != null) {
                        existingAdmin.setFonction(administrateur.getFonction());
                    }
                    // Mettre à jour le mot de passe seulement si fourni
                    if (administrateur.getMotDePasse() != null && !administrateur.getMotDePasse().isEmpty()) {
                        existingAdmin.setMotDePasse(administrateur.getMotDePasse());
                    }
                    return administrateurRepository.save(existingAdmin);
                })
                .orElse(null);
    }

    public void deleteAdministrateur(Long id) {
        administrateurRepository.deleteById(id);
    }
}
