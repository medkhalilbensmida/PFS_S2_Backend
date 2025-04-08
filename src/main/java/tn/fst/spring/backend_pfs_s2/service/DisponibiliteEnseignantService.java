package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.fst.spring.backend_pfs_s2.model.DisponibiliteEnseignant;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import tn.fst.spring.backend_pfs_s2.model.Surveillance;
import tn.fst.spring.backend_pfs_s2.repository.DisponibiliteEnseignantRepository;

import java.util.List;

@Service
public class DisponibiliteEnseignantService {

    private final DisponibiliteEnseignantRepository disponibiliteRepository;

    public DisponibiliteEnseignantService(DisponibiliteEnseignantRepository disponibiliteRepository) {
        this.disponibiliteRepository = disponibiliteRepository;
    }

    @Transactional(readOnly = true)
    public List<DisponibiliteEnseignant> getDisponibilitesByEnseignant(Long enseignantId) {
        return disponibiliteRepository.findByEnseignantId(enseignantId);
    }

    @Transactional(readOnly = true)
    public List<DisponibiliteEnseignant> getAllDisponibilites() {
        return disponibiliteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<DisponibiliteEnseignant> getDisponibilitesBySurveillance(Long surveillanceId) {
        return disponibiliteRepository.findBySurveillanceId(surveillanceId);
    }

    @Transactional
    public DisponibiliteEnseignant updateDisponibilite(Long id, Boolean estDisponible) {
        DisponibiliteEnseignant disponibilite = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilité introuvable avec l'id: " + id));
        disponibilite.setEstDisponible(estDisponible);
        return disponibiliteRepository.save(disponibilite);
    }


    @Transactional
    public DisponibiliteEnseignant updateDisponibiliteBySurveillance(Long surveillanceId, Long enseignantId, Boolean estDisponible) {
        // Create Enseignant and Surveillance objects properly
        Enseignant enseignant = new Enseignant();
        enseignant.setId(enseignantId);
        
        Surveillance surveillance = new Surveillance();
        surveillance.setId(surveillanceId);
        
        // Find the disponibilite with properly initialized objects
        DisponibiliteEnseignant disponibilite = disponibiliteRepository
                .findByEnseignantAndSurveillance(enseignant, surveillance)
                .orElseThrow(() -> new RuntimeException("Disponibilité introuvable pour l'enseignant et la surveillance spécifiés"));
        
        disponibilite.setEstDisponible(estDisponible);
        return disponibiliteRepository.save(disponibilite);
    }
}