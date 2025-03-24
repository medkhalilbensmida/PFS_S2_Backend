package tn.fst.spring.backend_pfs_s2.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import tn.fst.spring.backend_pfs_s2.model.*;
import tn.fst.spring.backend_pfs_s2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdministrateurRepository administrateurRepository;

    @Autowired
    private EnseignantRepository enseignantRepository;

    @Autowired
    private SessionExamenRepository sessionExamenRepository;

    @Autowired
    private SurveillanceRepository surveillanceRepository;

    @Autowired
    private MatiereRepository matiereRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SalleRepository salleRepository;

    @Autowired
    private AnneeUniversitaireRepository anneeUniversitaireRepository;

    @Autowired
    private EnseigneRepository enseigneRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private DisponibiliteEnseignantRepository disponibiliteRepository;

    @Override
    public void run(String... args) throws Exception {
        // Insérer des données uniquement si elles n'existent pas déjà
        insertAdministrateurs();
        insertEnseignants();
        insertAnneesUniversitaires();
        insertMatieres();
        insertSalles();
        insertSessionsExamen();
        insertSurveillances();
        insertEnseignes();
        insertNotifications();
        initDisponibilitesForAllSurveillances();
    }

    private void initDisponibilitesForAllSurveillances() {
        List<Surveillance> surveillances = surveillanceRepository.findAll();
        List<Enseignant> enseignants = enseignantRepository.findAll();

        for (Surveillance surveillance : surveillances) {
            for (Enseignant enseignant : enseignants) {
                if (!disponibiliteRepository.existsByEnseignantAndSurveillance(enseignant, surveillance)) {
                    DisponibiliteEnseignant disponibilite = new DisponibiliteEnseignant();
                    disponibilite.setEnseignant(enseignant);
                    disponibilite.setSurveillance(surveillance);
                    disponibilite.setEstDisponible(false); // Initialement non disponible
                    disponibiliteRepository.save(disponibilite);
                }
            }
        }
    }

    private void insertSurveillances() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<SessionExamen> sessions = sessionExamenRepository.findAll();
        List<Salle> salles = salleRepository.findAll();
        List<Matiere> matieres = matiereRepository.findAll();
        List<Enseignant> enseignants = enseignantRepository.findAll();

        // Création des surveillances sans enseignants initialement
        List<Surveillance> surveillances = Arrays.asList(
                createSurveillance(dateFormat.parse("2023-12-15 09:00"), dateFormat.parse("2023-12-15 11:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(0), matieres.get(0), sessions.get(0)),
                createSurveillance(dateFormat.parse("2023-12-15 14:00"), dateFormat.parse("2023-12-15 16:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(1), matieres.get(1), sessions.get(0)),
                createSurveillance(dateFormat.parse("2023-12-16 09:00"), dateFormat.parse("2023-12-16 11:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(2), matieres.get(2), sessions.get(1)),
                createSurveillance(dateFormat.parse("2023-12-16 14:00"), dateFormat.parse("2023-12-16 16:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(3), matieres.get(3), sessions.get(1)),
                createSurveillance(dateFormat.parse("2023-12-17 09:00"), dateFormat.parse("2023-12-17 11:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(4), matieres.get(4), sessions.get(2)),
                createSurveillance(dateFormat.parse("2023-12-17 14:00"), dateFormat.parse("2023-12-17 16:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(5), matieres.get(5), sessions.get(2)),
                createSurveillance(dateFormat.parse("2023-12-18 09:00"), dateFormat.parse("2023-12-18 11:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(6), matieres.get(6), sessions.get(3)),
                createSurveillance(dateFormat.parse("2023-12-18 14:00"), dateFormat.parse("2023-12-18 16:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(7), matieres.get(7), sessions.get(3)),
                createSurveillance(dateFormat.parse("2023-12-19 09:00"), dateFormat.parse("2023-12-19 11:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(8), matieres.get(8), sessions.get(4)),
                createSurveillance(dateFormat.parse("2023-12-19 14:00"), dateFormat.parse("2023-12-19 16:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(9), matieres.get(9), sessions.get(4))
        );

        for (Surveillance surveillance : surveillances) {
            if (!surveillanceRepository.existsByDateDebutAndDateFin(surveillance.getDateDebut(), surveillance.getDateFin())) {
                surveillanceRepository.save(surveillance);
            }
        }
    }

    private Surveillance createSurveillance(Date dateDebut, Date dateFin, StatutSurveillance statut,
                                            Salle salle, Matiere matiere, SessionExamen sessionExamen) {
        Surveillance surveillance = new Surveillance();
        surveillance.setDateDebut(dateDebut);
        surveillance.setDateFin(dateFin);
        surveillance.setStatut(statut);
        surveillance.setSalle(salle);
        surveillance.setMatiere(matiere);
        surveillance.setSessionExamen(sessionExamen);
        return surveillance;
    }

    private void insertAdministrateurs() {
        List<Administrateur> administrateurs = Arrays.asList(
                new Administrateur("Admin1", "Doe", "admin1@example.com", "password1", "123456789", "Directeur"),
                new Administrateur("Admin2", "Smith", "admin2@example.com", "password2", "987654321", "Responsable"),
                new Administrateur("Admin3", "Johnson", "admin3@example.com", "password3", "111111111", "Secrétaire"),
                new Administrateur("Admin4", "Brown", "admin4@example.com", "password4", "222222222", "Coordinateur"),
                new Administrateur("Admin5", "Davis", "admin5@example.com", "password5", "333333333", "Gestionnaire"),
                new Administrateur("Admin6", "Wilson", "admin6@example.com", "password6", "444444444", "Superviseur"),
                new Administrateur("Admin7", "Moore", "admin7@example.com", "password7", "555555555", "Chef de département"),
                new Administrateur("Admin8", "Taylor", "admin8@example.com", "password8", "666666666", "Responsable RH"),
                new Administrateur("Admin9", "Anderson", "admin9@example.com", "password9", "777777777", "Responsable pédagogique"),
                new Administrateur("Admin10", "Thomas", "admin10@example.com", "password10", "888888888", "Responsable administratif")
        );

        for (Administrateur admin : administrateurs) {
            if (!administrateurRepository.existsByEmail(admin.getEmail())) {
                admin.setMotDePasse(passwordEncoder.encode(admin.getMotDePasse()));
                administrateurRepository.save(admin);
            }
        }
    }

    private void insertEnseignants() {
        List<Enseignant> enseignants = Arrays.asList(
                new Enseignant("Jean", "Dupont", "jean.dupont@example.com", "password1", "123456789", "Professeur", "Informatique"),
                new Enseignant("Marie", "Curie", "marie.curie@example.com", "password2", "987654321", "Maître de conférences", "Mathématiques"),
                new Enseignant("Pierre", "Durand", "pierre.durand@example.com", "password3", "111111111", "Professeur", "Physique"),
                new Enseignant("Sophie", "Martin", "sophie.martin@example.com", "password4", "222222222", "Maître de conférences", "Chimie"),
                new Enseignant("Luc", "Bernard", "luc.bernard@example.com", "password5", "333333333", "Professeur", "Biologie"),
                new Enseignant("Emma", "Petit", "emma.petit@example.com", "password6", "444444444", "Maître de conférences", "Géologie"),
                new Enseignant("Louis", "Robert", "louis.robert@example.com", "password7", "555555555", "Professeur", "Informatique"),
                new Enseignant("Chloé", "Richard", "chloe.richard@example.com", "password8", "666666666", "Maître de conférences", "Mathématiques"),
                new Enseignant("Hugo", "Durand", "hugo.durand@example.com", "password9", "777777777", "Professeur", "Physique"),
                new Enseignant("Alice", "Moreau", "alice.moreau@example.com", "password10", "888888888", "Maître de conférences", "Chimie")
        );

        for (Enseignant enseignant : enseignants) {
            if (!enseignantRepository.existsByEmail(enseignant.getEmail())) {
                enseignant.setMotDePasse(passwordEncoder.encode(enseignant.getMotDePasse()));
                enseignantRepository.save(enseignant);
            }
        }
    }
    private void insertAnneesUniversitaires() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<AnneeUniversitaire> annees = Arrays.asList(
                new AnneeUniversitaire(dateFormat.parse("2023-09-01"), dateFormat.parse("2024-06-30"), true),
                new AnneeUniversitaire(dateFormat.parse("2024-09-01"), dateFormat.parse("2025-06-30"), false),
                new AnneeUniversitaire(dateFormat.parse("2025-09-01"), dateFormat.parse("2026-06-30"), false),
                new AnneeUniversitaire(dateFormat.parse("2026-09-01"), dateFormat.parse("2027-06-30"), false),
                new AnneeUniversitaire(dateFormat.parse("2027-09-01"), dateFormat.parse("2028-06-30"), false)
        );

        for (AnneeUniversitaire annee : annees) {
            if (anneeUniversitaireRepository.findByDateDebutAndDateFin(annee.getDateDebut(), annee.getDateFin()).isEmpty()) {
                anneeUniversitaireRepository.save(annee);
            }
        }
    }

    private void insertMatieres() {
        List<Matiere> matieres = Arrays.asList(
                new Matiere("L1", "Informatique", "INF101", "Programmation Java"),
                new Matiere("L2", "Mathématiques", "MAT201", "Algèbre linéaire"),
                new Matiere("L1", "Physique", "PHY101", "Mécanique classique"),
                new Matiere("L2", "Chimie", "CHM201", "Chimie organique"),
                new Matiere("L1", "Biologie", "BIO101", "Biologie cellulaire"),
                new Matiere("L2", "Géologie", "GEO201", "Géologie structurale"),
                new Matiere("L1", "Informatique", "INF102", "Structures de données"),
                new Matiere("L2", "Mathématiques", "MAT202", "Analyse numérique"),
                new Matiere("L1", "Physique", "PHY102", "Électromagnétisme"),
                new Matiere("L2", "Chimie", "CHM202", "Chimie inorganique")
        );

        for (Matiere matiere : matieres) {
            if (matiereRepository.findByCode(matiere.getCode()).isEmpty()) {
                matiereRepository.save(matiere);
            }
        }
    }

    private void insertSalles() {
        List<Salle> salles = Arrays.asList(
                new Salle("A101", 50, "Bâtiment A", "1er étage"),
                new Salle("B202", 100, "Bâtiment B", "2e étage"),
                new Salle("C303", 75, "Bâtiment C", "3e étage"),
                new Salle("D404", 60, "Bâtiment D", "4e étage"),
                new Salle("E505", 80, "Bâtiment E", "5e étage"),
                new Salle("F606", 90, "Bâtiment F", "6e étage"),
                new Salle("G707", 70, "Bâtiment G", "7e étage"),
                new Salle("H808", 85, "Bâtiment H", "8e étage"),
                new Salle("I909", 65, "Bâtiment I", "9e étage"),
                new Salle("J1010", 95, "Bâtiment J", "10e étage")
        );

        for (Salle salle : salles) {
            if (salleRepository.findByNumero(salle.getNumero()).isEmpty()) {
                salleRepository.save(salle);
            }
        }
    }

    private void insertSessionsExamen() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        AnneeUniversitaire annee = anneeUniversitaireRepository.findById(1L).orElseThrow();

        List<SessionExamen> sessions = Arrays.asList(
                new SessionExamen(dateFormat.parse("2023-12-15"), dateFormat.parse("2023-12-22"),
                        TypeSession.PRINCIPALE, true, annee, Semestre.S1),
                new SessionExamen(dateFormat.parse("2024-01-15"), dateFormat.parse("2024-01-22"),
                        TypeSession.RATTRAPAGE, false, annee, Semestre.S2),
                new SessionExamen(dateFormat.parse("2024-05-15"), dateFormat.parse("2024-05-22"),
                        TypeSession.PRINCIPALE, true, annee, Semestre.S1),
                new SessionExamen(dateFormat.parse("2024-06-15"), dateFormat.parse("2024-06-22"),
                        TypeSession.RATTRAPAGE, false, annee, Semestre.S2),
                new SessionExamen(dateFormat.parse("2024-12-15"), dateFormat.parse("2024-12-22"),
                        TypeSession.PRINCIPALE, true, annee, Semestre.S1)
        );

        for (SessionExamen session : sessions) {
            if (sessionExamenRepository.findByDateDebutAndDateFin(session.getDateDebut(), session.getDateFin()).isEmpty()) {
                sessionExamenRepository.save(session);
            }
        }
    }

    private void insertEnseignes() {
        List<Enseignant> enseignants = enseignantRepository.findAll();
        List<Matiere> matieres = matiereRepository.findAll();
        AnneeUniversitaire annee = anneeUniversitaireRepository.findById(1L).orElseThrow();

        List<Enseigne> enseignes = Arrays.asList(
                new Enseigne(enseignants.get(0), matieres.get(0), Semestre.S1, annee, TypeMatiere.COURS),
                new Enseigne(enseignants.get(1), matieres.get(1), Semestre.S2, annee, TypeMatiere.TD),
                new Enseigne(enseignants.get(2), matieres.get(2), Semestre.S1, annee, TypeMatiere.TP),
                new Enseigne(enseignants.get(3), matieres.get(3), Semestre.S2, annee, TypeMatiere.COURS),
                new Enseigne(enseignants.get(4), matieres.get(4), Semestre.S1, annee, TypeMatiere.TD),
                new Enseigne(enseignants.get(5), matieres.get(5), Semestre.S2, annee, TypeMatiere.TP),
                new Enseigne(enseignants.get(6), matieres.get(6), Semestre.S1, annee, TypeMatiere.COURS),
                new Enseigne(enseignants.get(7), matieres.get(7), Semestre.S2, annee, TypeMatiere.TD),
                new Enseigne(enseignants.get(8), matieres.get(8), Semestre.S1, annee, TypeMatiere.TP),
                new Enseigne(enseignants.get(9), matieres.get(9), Semestre.S2, annee, TypeMatiere.COURS)
        );

        for (Enseigne enseigne : enseignes) {
            if (enseigneRepository.findByEnseignantAndMatiere(enseigne.getEnseignant(), enseigne.getMatiere()).isEmpty()) {
                enseigneRepository.save(enseigne);
            }
        }
    }

    private void insertNotifications() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<Enseignant> enseignants = enseignantRepository.findAll();
        List<Surveillance> surveillances = surveillanceRepository.findAll();

        List<Notification> notifications = Arrays.asList(
                new Notification("Vous avez été assigné à une surveillance.", dateFormat.parse("2023-12-14 10:00"), false, TypeNotification.AFFECTATION, enseignants.get(0), surveillances.get(0)),
                new Notification("Rappel : Surveillance demain à 10h.", dateFormat.parse("2023-12-14 18:00"), false, TypeNotification.RAPPEL, enseignants.get(1), surveillances.get(1)),
                new Notification("Vous avez été assigné à une surveillance.", dateFormat.parse("2023-12-15 10:00"), false, TypeNotification.AFFECTATION, enseignants.get(2), surveillances.get(2)),
                new Notification("Rappel : Surveillance demain à 10h.", dateFormat.parse("2023-12-15 18:00"), false, TypeNotification.RAPPEL, enseignants.get(3), surveillances.get(3)),
                new Notification("Vous avez été assigné à une surveillance.", dateFormat.parse("2023-12-16 10:00"), false, TypeNotification.AFFECTATION, enseignants.get(4), surveillances.get(4)),
                new Notification("Rappel : Surveillance demain à 10h.", dateFormat.parse("2023-12-16 18:00"), false, TypeNotification.RAPPEL, enseignants.get(5), surveillances.get(5)),
                new Notification("Vous avez été assigné à une surveillance.", dateFormat.parse("2023-12-17 10:00"), false, TypeNotification.AFFECTATION, enseignants.get(6), surveillances.get(6)),
                new Notification("Rappel : Surveillance demain à 10h.", dateFormat.parse("2023-12-17 18:00"), false, TypeNotification.RAPPEL, enseignants.get(7), surveillances.get(7)),
                new Notification("Vous avez été assigné à une surveillance.", dateFormat.parse("2023-12-18 10:00"), false, TypeNotification.AFFECTATION, enseignants.get(8), surveillances.get(8)),
                new Notification("Rappel : Surveillance demain à 10h.", dateFormat.parse("2023-12-18 18:00"), false, TypeNotification.RAPPEL, enseignants.get(9), surveillances.get(9))
        );

        for (Notification notification : notifications) {
            if (notificationRepository.findByMessage(notification.getMessage()).isEmpty()) {
                notificationRepository.save(notification);
            }
        }
    }
}