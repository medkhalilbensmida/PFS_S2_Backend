package tn.fst.spring.backend_pfs_s2.service;

import jakarta.persistence.EntityNotFoundException; // Importation ajoutée
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import org.springframework.transaction.annotation.Transactional; // Importation ajoutée
import tn.fst.spring.backend_pfs_s2.model.*;
import tn.fst.spring.backend_pfs_s2.repository.*;

import java.util.Date; // Importation ajoutée (si Date est utilisé directement)
import java.util.List;
import java.util.stream.Collectors;


import java.io.IOException;
import java.io.OutputStream;

import tn.fst.spring.backend_pfs_s2.service.export.CsvExportService;
import tn.fst.spring.backend_pfs_s2.service.export.ExcelExportService;
import tn.fst.spring.backend_pfs_s2.service.export.SurveillanceFilterDTO;

import java.util.Optional; // Importation ajoutée (si Optional est utilisé)

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



    // Injecter les autres repositories nécessaires pour la validation des clés étrangères
    @Autowired
    private SalleRepository salleRepository;

    @Autowired
    private MatiereRepository matiereRepository;

    @Autowired
    private SessionExamenRepository sessionExamenRepository;

    // Injection optionnelle pour les notifications
    // @Autowired
    // private NotificationService notificationService;


    public List<Surveillance> getAllSurveillances() {
        return surveillanceRepository.findAll();
    }

    public Surveillance getSurveillanceById(Long id) {
        return surveillanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Surveillance non trouvée avec l'ID : " + id)); // Modifié pour lancer une exception si non trouvé
    }

    @Transactional
    public Surveillance createSurveillance(Surveillance surveillance) {
        // Valider les entités liées avant de sauvegarder
        validateForeignKeyEntities(surveillance);

        // Check for overlapping surveillances in the same salle
        if (surveillance.getSalle() != null) {
            Long salleId = surveillance.getSalle().getId();

            if (salleId != null) {
                boolean overlapping = surveillanceRepository.existsOverlappingSurveillanceForSalle(
                        salleId,
                        surveillance.getDateDebut(),
                        surveillance.getDateFin(),
                        null); // null for excludeSurveillanceId since this is a new surveillance

                if (overlapping) {
                    throw new IllegalStateException("Il existe deja une surveillance dans cette salle pendant cette periode.");
                }
            }
        }

        // Assurer que les enseignants ne sont pas définis à la création
        surveillance.setEnseignantPrincipal(null);
        surveillance.setEnseignantSecondaire(null);
        Surveillance savedSurveillance = surveillanceRepository.save(surveillance);
        initDisponibilitesForSurveillance(savedSurveillance);
        return savedSurveillance;
    }

    private void initDisponibilitesForSurveillance(Surveillance surveillance) {
        List<Enseignant> enseignants = enseignantRepository.findAll();
        for (Enseignant enseignant : enseignants) {
            // Vérifie si une disponibilité existe déjà pour éviter les doublons
            if (!disponibiliteRepository.existsByEnseignantAndSurveillance(enseignant, surveillance)) {
                DisponibiliteEnseignant disponibilite = new DisponibiliteEnseignant();
                disponibilite.setEnseignant(enseignant);
                disponibilite.setSurveillance(surveillance);
                disponibilite.setEstDisponible(false); // Par défaut non disponible
                disponibiliteRepository.save(disponibilite);
            }
        }
    }

    @Transactional
    public Surveillance updateSurveillance(Long id, Surveillance surveillanceDetails) {
        Surveillance surveillance = surveillanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Surveillance non trouvée avec l'ID : " + id));

        // Check for overlapping surveillances in the same salle
        if (surveillanceDetails.getSalle() != null) {
            Long salleId = surveillanceDetails.getSalle().getId();
            if (salleId != null && surveillanceRepository.existsOverlappingSurveillanceForSalle(
                    salleId,
                    surveillanceDetails.getDateDebut(),
                    surveillanceDetails.getDateFin(),
                    id)) { // Pass the current surveillance ID to exclude it from the check
                throw new IllegalStateException("Il existe deja une surveillance dans cette salle pendant cette periode.");
            }
        }

        // Mettre à jour les champs simples
        surveillance.setDateDebut(surveillanceDetails.getDateDebut());
        surveillance.setDateFin(surveillanceDetails.getDateFin());
        surveillance.setStatut(surveillanceDetails.getStatut());

        // Mettre à jour les relations (valide les entités liées avant de les setter)
        validateForeignKeyEntities(surveillanceDetails);
        surveillance.setSalle(surveillanceDetails.getSalle());
        surveillance.setMatiere(surveillanceDetails.getMatiere());
        surveillance.setSessionExamen(surveillanceDetails.getSessionExamen());

        // La logique d'affectation des enseignants est gérée séparément par assignEnseignants
        // On s'assure de ne pas écraser les affectations existantes par cette méthode

        return surveillanceRepository.save(surveillance);
    }


    @Transactional // Assure que toutes les opérations se font dans une seule transaction
    public Surveillance assignEnseignants(Long surveillanceId, Long enseignantPrincipalId, Long enseignantSecondaireId) {
        Surveillance surveillance = surveillanceRepository.findById(surveillanceId)
                .orElseThrow(() -> new EntityNotFoundException("Surveillance non trouvée avec l'ID : " + surveillanceId));

        Enseignant enseignantPrincipal = null;
        if (enseignantPrincipalId != null) {
            enseignantPrincipal = enseignantRepository.findById(enseignantPrincipalId)
                    .orElseThrow(() -> new EntityNotFoundException("Enseignant Principal non trouvé avec l'ID : " + enseignantPrincipalId));

            // 1. Validation de la disponibilité déclarée par l'enseignant
            validateEnseignantDisponibilite(enseignantPrincipal, surveillance);

            // 2. Vérification des conflits d'horaire
            checkForConflicts(enseignantPrincipal, surveillance);
        }

        Enseignant enseignantSecondaire = null;
        if (enseignantSecondaireId != null) {
            // Vérifier que l'enseignant secondaire n'est pas le même que le principal
            if (enseignantPrincipalId != null && enseignantPrincipalId.equals(enseignantSecondaireId)) {
                throw new IllegalArgumentException("L'enseignant principal et secondaire ne peuvent pas être la même personne.");
            }
            enseignantSecondaire = enseignantRepository.findById(enseignantSecondaireId)
                    .orElseThrow(() -> new EntityNotFoundException("Enseignant Secondaire non trouvé avec l'ID : " + enseignantSecondaireId));

            // 1. Validation de la disponibilité déclarée par l'enseignant
            validateEnseignantDisponibilite(enseignantSecondaire, surveillance);

            // 2. Vérification des conflits d'horaire
            checkForConflicts(enseignantSecondaire, surveillance);
        }


        // Affectation (ou désaffectation si l'ID est null)
        surveillance.setEnseignantPrincipal(enseignantPrincipal);
        surveillance.setEnseignantSecondaire(enseignantSecondaire);

        // Sauvegarde de la surveillance mise à jour
        Surveillance updatedSurveillance = surveillanceRepository.save(surveillance);

        // TODO: Ajouter la logique de notification ici si nécessaire
        // Exemple:
        // if (enseignantPrincipal != null) {
        //     notificationService.createNotification(new Notification(
        //         "Vous avez été affecté comme principal à la surveillance " + surveillance.getId(),
        //         new Date(), false, TypeNotification.AFFECTATION, enseignantPrincipal, surveillance
        //     ));
        // }
        // if (enseignantSecondaire != null) {
        //      notificationService.createNotification(new Notification(
        //         "Vous avez été affecté comme secondaire à la surveillance " + surveillance.getId(),
        //         new Date(), false, TypeNotification.AFFECTATION, enseignantSecondaire, surveillance
        //     ));
        // }
        // Gérer aussi les notifications de désaffectation si un enseignant est remplacé

        return updatedSurveillance;
    }

    // Méthode helper pour valider la disponibilité
    private void validateEnseignantDisponibilite(Enseignant enseignant, Surveillance surveillance) {
        DisponibiliteEnseignant disponibilite = disponibiliteRepository
                .findByEnseignantAndSurveillance(enseignant, surveillance)
                .orElse(null); // La disponibilité devrait toujours exister après initDisponibilitesForSurveillance

        // Si la disponibilité n'est pas trouvée OU si l'enseignant n'est pas marqué comme disponible
        if (disponibilite == null || !Boolean.TRUE.equals(disponibilite.getEstDisponible())) {
            throw new IllegalStateException("L'enseignant " + enseignant.getPrenom() + " " + enseignant.getNom() +
                    " n'est pas disponible pour la surveillance ID: " + surveillance.getId());
        }
    }

    // Méthode helper pour vérifier les conflits
    private void checkForConflicts(Enseignant enseignant, Surveillance targetSurveillance) {
        List<Surveillance> conflictingSurveillances = surveillanceRepository.findOverlappingSurveillancesForEnseignant(
                enseignant.getId(),
                targetSurveillance.getDateDebut(),
                targetSurveillance.getDateFin(),
                targetSurveillance.getId() // Exclure la surveillance actuelle de la vérification
        );

        if (!conflictingSurveillances.isEmpty()) {
            StringBuilder conflictDetails = new StringBuilder();
            conflictingSurveillances.forEach(s -> conflictDetails.append(" [ID: ").append(s.getId())
                    .append(", Heure: ").append(s.getDateDebut())
                    .append("-").append(s.getDateFin()).append("]"));

            throw new IllegalStateException("Conflit d'horaire pour l'enseignant " + enseignant.getPrenom() + " " + enseignant.getNom() +
                    ". Il/Elle est déjà affecté(e) aux surveillances suivantes:" + conflictDetails.toString());
        }
    }

    @Transactional
    public void deleteSurveillance(Long id) {
        if (!surveillanceRepository.existsById(id)) {
            throw new EntityNotFoundException("Surveillance non trouvée avec l'ID : " + id);
        }
        // Supprimer d'abord les disponibilités associées
        List<DisponibiliteEnseignant> disponibilites =
                disponibiliteRepository.findBySurveillanceId(id);
        disponibiliteRepository.deleteAll(disponibilites);

        // Supprimer les notifications associées (optionnel, dépend de vos règles métier)
        // List<Notification> notifications = notificationRepository.findBySurveillanceId(id);
        // notificationRepository.deleteAll(notifications);

        // Puis supprimer la surveillance
        surveillanceRepository.deleteById(id);
    }


    // Méthode pour valider l'existence des entités liées avant sauvegarde/mise à jour
    private void validateForeignKeyEntities(Surveillance surveillance) {
        // Utilise les repositories injectés pour vérifier l'existence par ID
        if (surveillance.getSalle() != null && surveillance.getSalle().getId() != null && !salleRepository.existsById(surveillance.getSalle().getId())) {
            throw new EntityNotFoundException("Salle non trouvée avec l'ID : " + surveillance.getSalle().getId());
        }
        if (surveillance.getMatiere() != null && surveillance.getMatiere().getId() != null && !matiereRepository.existsById(surveillance.getMatiere().getId())) {
            throw new EntityNotFoundException("Matière non trouvée avec l'ID : " + surveillance.getMatiere().getId());
        }
        if (surveillance.getSessionExamen() != null && surveillance.getSessionExamen().getId() != null && !sessionExamenRepository.existsById(surveillance.getSessionExamen().getId())) {
            throw new EntityNotFoundException("Session d'examen non trouvée avec l'ID : " + surveillance.getSessionExamen().getId());
        }
        // La validation des enseignants (si présents dans l'objet) se fait dans assignEnseignants
    }

    // --- Méthodes liées aux disponibilités (peuvent rester ou être déplacées dans DisponibiliteEnseignantService si préféré) ---

    public List<DisponibiliteEnseignant> getDisponibilitesForSurveillance(Long surveillanceId) {
        if (!surveillanceRepository.existsById(surveillanceId)) {
            throw new EntityNotFoundException("Surveillance non trouvée avec l'ID : " + surveillanceId);
        }
        return disponibiliteRepository.findBySurveillanceId(surveillanceId);
    }

    public List<DisponibiliteEnseignant> getDisponibilitesForEnseignant(Long enseignantId) {
        if (!enseignantRepository.existsById(enseignantId)) {
            throw new EntityNotFoundException("Enseignant non trouvé avec l'ID : " + enseignantId);
        }
        return disponibiliteRepository.findByEnseignantId(enseignantId);
    }

    // Cette méthode est également présente dans DisponibiliteEnseignantService,
    // choisissez où la conserver pour éviter la duplication.
    // Si elle reste ici, assurez-vous qu'elle est appelée correctement ou supprimez-la
    // si DisponibiliteEnseignantService est utilisé à la place.
    @Transactional
    public DisponibiliteEnseignant updateDisponibilite(Long id, Boolean estDisponible) {
        DisponibiliteEnseignant disponibilite = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Disponibilité non trouvée avec l'ID : " + id));
        disponibilite.setEstDisponible(estDisponible);
        return disponibiliteRepository.save(disponibilite);
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

    private boolean isEnseignantAvailable(Enseignant enseignant, Surveillance surveillance) {
        if (enseignant == null) return false;

        List<DisponibiliteEnseignant> disponibilites = disponibiliteRepository
                .findBySurveillanceId(surveillance.getId());

        // Check if any disponibilité exists for this teacher and is true
        return disponibilites.stream()
                .filter(d -> d.getEnseignant().getId().equals(enseignant.getId()))
                .anyMatch(DisponibiliteEnseignant::getEstDisponible);
    }

    public List<Surveillance> getSurveillancesByEnseignant(Long enseignantId) {
        return surveillanceRepository.findAll().stream()
                .filter(s -> (s.getEnseignantPrincipal() != null && s.getEnseignantPrincipal().getId().equals(enseignantId)) ||
                        (s.getEnseignantSecondaire() != null && s.getEnseignantSecondaire().getId().equals(enseignantId)))
                .collect(Collectors.toList());
    }

    public List<Surveillance> filterSurveillances(SurveillanceFilterDTO filterDTO) {
        return surveillanceRepository.findAll().stream()
                .filter(surveillance -> {
                    boolean matches = true;

                    if (filterDTO.getAnneeUniversitaire() != null && !filterDTO.getAnneeUniversitaire().isEmpty()) {
                        String[] years = filterDTO.getAnneeUniversitaire().split("-");
                        if (years.length == 2) {
                            int startYear = Integer.parseInt(years[0]);
                            int endYear = Integer.parseInt(years[1]);

                            // Get the year from surveillance's session
                            int surveillanceYear = surveillance.getSessionExamen().getAnnee().getDateDebut().getYear() + 1900;
                            matches = matches && (surveillanceYear == startYear);
                        }
                    }

                    if (filterDTO.getSemestre() != null) {
                        matches = matches && surveillance.getSessionExamen().getNumSemestre()
                                .equals(filterDTO.getSemestre());
                    }

                    if (filterDTO.getTypeSession() != null) {
                        matches = matches && surveillance.getSessionExamen().getType()
                                .equals(filterDTO.getTypeSession());
                    }

                    return matches;
                })
                .collect(Collectors.toList());
    }
    // Modify existing export methods
    public void exportToCsv(OutputStream outputStream, SurveillanceFilterDTO filterDTO) throws IOException {
        List<Surveillance> surveillances = filterSurveillances(filterDTO);
        csvExportService.export(surveillances, outputStream);
    }

    public void exportToExcel(OutputStream outputStream, SurveillanceFilterDTO filterDTO) throws IOException {
        List<Surveillance> surveillances = filterSurveillances(filterDTO);
        excelExportService.export(surveillances, outputStream);
    }


    // Method to get surveillances by session ID (Added to fix compilation error)
    public List<Surveillance> getSurveillancesBySessionId(Long sessionId) {
        if (sessionId == null) {
            // Or handle as appropriate, maybe return empty list or throw exception
            throw new IllegalArgumentException("Session ID cannot be null");
        }
        // Assuming SurveillanceRepository has this method
        // If not, you'll need to add 'List<Surveillance> findBySessionExamenId(Long sessionId);'
        // to the SurveillanceRepository interface.
        return surveillanceRepository.findBySessionExamenId(sessionId);
    }
}