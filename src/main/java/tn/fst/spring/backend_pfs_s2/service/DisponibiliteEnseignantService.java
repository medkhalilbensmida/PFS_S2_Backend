package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.model.DisponibiliteEnseignant;
import tn.fst.spring.backend_pfs_s2.repository.DisponibiliteEnseignantRepository;

import java.util.List;

@Service
public class DisponibiliteEnseignantService {

    @Autowired
    private DisponibiliteEnseignantRepository disponibiliteRepository;

    public List<DisponibiliteEnseignant> getDisponibilitesByEnseignant(Long enseignantId) {
        return disponibiliteRepository.findByEnseignantId(enseignantId);
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