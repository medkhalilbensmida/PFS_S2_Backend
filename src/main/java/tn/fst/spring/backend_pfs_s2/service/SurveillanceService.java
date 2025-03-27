package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import tn.fst.spring.backend_pfs_s2.model.*;
import tn.fst.spring.backend_pfs_s2.repository.*;

import java.util.List;
import java.util.stream.Collectors;


import java.io.IOException;
import java.io.OutputStream;

import tn.fst.spring.backend_pfs_s2.service.export.CsvExportService;
import tn.fst.spring.backend_pfs_s2.service.export.ExcelExportService;


@Service
public class SurveillanceService {

    @Autowired
    private SurveillanceRepository surveillanceRepository;

    @Autowired
    private EnseignantRepository enseignantRepository;

    @Autowired
    private DisponibiliteEnseignantRepository disponibiliteRepository;
    @Autowired
    private CsvExportService csvExportService;

    @Autowired
    private ExcelExportService excelExportService;



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




    public void exportToCsv(OutputStream outputStream) throws IOException {
        List<Surveillance> surveillances = getAvailableSurveillances();
        csvExportService.export(surveillances, outputStream);
    }

    public void exportToExcel(OutputStream outputStream) throws IOException {
        List<Surveillance> surveillances = getAvailableSurveillances();
        excelExportService.export(surveillances, outputStream);
    }

    // Add this method to SurveillanceService class
    public List<Surveillance> getAvailableSurveillances() {
        return surveillanceRepository.findAll().stream()
                .filter(surveillance ->
                        surveillance.getStatut() == StatutSurveillance.PLANIFIEE &&
                                isEnseignantAvailable(surveillance.getEnseignantPrincipal(), surveillance) &&
                                isEnseignantAvailable(surveillance.getEnseignantSecondaire(), surveillance))
                .collect(Collectors.toList());
    }

    // Add this helper method to check teacher availability
    private boolean isEnseignantAvailable(Enseignant enseignant, Surveillance surveillance) {
        if (enseignant == null) return false;

        List<DisponibiliteEnseignant> disponibilites = disponibiliteRepository
                .findByEnseignantAndSurveillance(enseignant, surveillance);

        // Check if any of the disponibilités is true
        return disponibilites.stream()
                .anyMatch(DisponibiliteEnseignant::getEstDisponible);
    }


    public List<Surveillance> getSurveillancesByEnseignant(Long enseignantId) {
        return surveillanceRepository.findAll().stream()
                .filter(s -> (s.getEnseignantPrincipal() != null && s.getEnseignantPrincipal().getId().equals(enseignantId)) ||
                        (s.getEnseignantSecondaire() != null && s.getEnseignantSecondaire().getId().equals(enseignantId)))
                .collect(Collectors.toList());
    }
}