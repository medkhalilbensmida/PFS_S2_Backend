package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.model.*;
import tn.fst.spring.backend_pfs_s2.repository.*;

import java.util.List;

@Service
public class SurveillanceService {

    @Autowired
    private SurveillanceRepository surveillanceRepository;

    @Autowired
    private EnseignantRepository enseignantRepository;

    @Autowired
    private DisponibiliteEnseignantRepository disponibiliteRepository;

    public List<Surveillance> getAllSurveillances() {
        return surveillanceRepository.findAll();
    }

    public Surveillance getSurveillanceById(Long id) {
        return surveillanceRepository.findById(id).orElse(null);
    }

    public Surveillance createSurveillance(Surveillance surveillance) {
        Surveillance savedSurveillance = surveillanceRepository.save(surveillance);
        initDisponibilitesForSurveillance(savedSurveillance);
        return savedSurveillance;
    }

    private void initDisponibilitesForSurveillance(Surveillance surveillance) {
        List<Enseignant> enseignants = enseignantRepository.findAll();
        for (Enseignant enseignant : enseignants) {
            DisponibiliteEnseignant disponibilite = new DisponibiliteEnseignant();
            disponibilite.setEnseignant(enseignant);
            disponibilite.setSurveillance(surveillance);
            disponibilite.setEstDisponible(false); // Par défaut non disponible
            disponibiliteRepository.save(disponibilite);
        }
    }

    public Surveillance updateSurveillance(Long id, Surveillance surveillance) {
        if (surveillanceRepository.existsById(id)) {
            surveillance.setId(id);
            return surveillanceRepository.save(surveillance);
        }
        return null;
    }

    public void deleteSurveillance(Long id) {
        // Supprimer d'abord les disponibilités associées
        List<DisponibiliteEnseignant> disponibilites =
                disponibiliteRepository.findBySurveillanceId(id);
        disponibiliteRepository.deleteAll(disponibilites);

        // Puis supprimer la surveillance
        surveillanceRepository.deleteById(id);
    }

    public List<DisponibiliteEnseignant> getDisponibilitesForSurveillance(Long surveillanceId) {
        return disponibiliteRepository.findBySurveillanceId(surveillanceId);
    }

    public List<DisponibiliteEnseignant> getDisponibilitesForEnseignant(Long enseignantId) {
        return disponibiliteRepository.findByEnseignantId(enseignantId);
    }

    public DisponibiliteEnseignant updateDisponibilite(Long id, Boolean estDisponible) {
        DisponibiliteEnseignant disponibilite = disponibiliteRepository.findById(id).orElse(null);
        if (disponibilite != null) {
            disponibilite.setEstDisponible(estDisponible);
            return disponibiliteRepository.save(disponibilite);
        }
        return null;
    }
}