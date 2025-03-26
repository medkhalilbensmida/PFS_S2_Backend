package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.model.DisponibiliteEnseignant;
import tn.fst.spring.backend_pfs_s2.repository.DisponibiliteEnseignantRepository;

import java.util.List;

@Service
public class DisponibiliteEnseignantService {

    private final DisponibiliteEnseignantRepository disponibiliteRepository;

    public DisponibiliteEnseignantService(DisponibiliteEnseignantRepository disponibiliteRepository) {
        this.disponibiliteRepository = disponibiliteRepository;
    }

    public List<DisponibiliteEnseignant> getDisponibilitesByEnseignant(Long enseignantId) {
        return disponibiliteRepository.findByEnseignantId(enseignantId);
    }

    public List<DisponibiliteEnseignant> getAllDisponibilites() {
        return disponibiliteRepository.findAll();
    }

    public List<DisponibiliteEnseignant> getDisponibilitesBySurveillance(Long surveillanceId) {
        return disponibiliteRepository.findBySurveillanceId(surveillanceId);
    }

    public DisponibiliteEnseignant updateDisponibilite(Long id, Boolean estDisponible) {
        DisponibiliteEnseignant disponibilite = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilité not found with id: " + id));
        disponibilite.setEstDisponible(estDisponible);
        return disponibiliteRepository.save(disponibilite);
    }
}