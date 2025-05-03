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
                    disponibilite.setEstDisponible(Math.random() > 0.7); // 30% de chance d'être disponible
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

        // Création de 50 surveillances variées
        List<Surveillance> surveillances = Arrays.asList(
                // Session 1
                createSurveillance(dateFormat.parse("2023-12-15 08:00"), dateFormat.parse("2023-12-15 10:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(0), matieres.get(0), sessions.get(0)),
                createSurveillance(dateFormat.parse("2023-12-15 10:30"), dateFormat.parse("2023-12-15 12:30"),
                        StatutSurveillance.PLANIFIEE, salles.get(1), matieres.get(1), sessions.get(0)),
                createSurveillance(dateFormat.parse("2023-12-15 14:00"), dateFormat.parse("2023-12-15 16:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(2), matieres.get(2), sessions.get(0)),
                createSurveillance(dateFormat.parse("2023-12-16 08:00"), dateFormat.parse("2023-12-16 10:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(3), matieres.get(3), sessions.get(0)),
                createSurveillance(dateFormat.parse("2023-12-16 10:30"), dateFormat.parse("2023-12-16 12:30"),
                        StatutSurveillance.PLANIFIEE, salles.get(4), matieres.get(4), sessions.get(0)),

                // Session 2
                createSurveillance(dateFormat.parse("2024-01-15 08:00"), dateFormat.parse("2024-01-15 10:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(5), matieres.get(5), sessions.get(1)),
                createSurveillance(dateFormat.parse("2024-01-15 10:30"), dateFormat.parse("2024-01-15 12:30"),
                        StatutSurveillance.PLANIFIEE, salles.get(6), matieres.get(6), sessions.get(1)),
                createSurveillance(dateFormat.parse("2024-01-16 08:00"), dateFormat.parse("2024-01-16 10:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(7), matieres.get(7), sessions.get(1)),
                createSurveillance(dateFormat.parse("2024-01-16 10:30"), dateFormat.parse("2024-01-16 12:30"),
                        StatutSurveillance.PLANIFIEE, salles.get(8), matieres.get(8), sessions.get(1)),
                createSurveillance(dateFormat.parse("2024-01-17 08:00"), dateFormat.parse("2024-01-17 10:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(9), matieres.get(9), sessions.get(1)),

                // Session 3
                createSurveillance(dateFormat.parse("2024-05-15 08:00"), dateFormat.parse("2024-05-15 10:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(0), matieres.get(0), sessions.get(2)),
                createSurveillance(dateFormat.parse("2024-05-15 10:30"), dateFormat.parse("2024-05-15 12:30"),
                        StatutSurveillance.PLANIFIEE, salles.get(1), matieres.get(1), sessions.get(2)),
                createSurveillance(dateFormat.parse("2024-05-16 08:00"), dateFormat.parse("2024-05-16 10:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(2), matieres.get(2), sessions.get(2)),
                createSurveillance(dateFormat.parse("2024-05-16 10:30"), dateFormat.parse("2024-05-16 12:30"),
                        StatutSurveillance.PLANIFIEE, salles.get(3), matieres.get(3), sessions.get(2)),
                createSurveillance(dateFormat.parse("2024-05-17 08:00"), dateFormat.parse("2024-05-17 10:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(4), matieres.get(4), sessions.get(2)),

                // Session 4
                createSurveillance(dateFormat.parse("2024-06-15 08:00"), dateFormat.parse("2024-06-15 10:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(5), matieres.get(5), sessions.get(3)),
                createSurveillance(dateFormat.parse("2024-06-15 10:30"), dateFormat.parse("2024-06-15 12:30"),
                        StatutSurveillance.PLANIFIEE, salles.get(6), matieres.get(6), sessions.get(3)),
                createSurveillance(dateFormat.parse("2024-06-16 08:00"), dateFormat.parse("2024-06-16 10:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(7), matieres.get(7), sessions.get(3)),
                createSurveillance(dateFormat.parse("2024-06-16 10:30"), dateFormat.parse("2024-06-16 12:30"),
                        StatutSurveillance.PLANIFIEE, salles.get(8), matieres.get(8), sessions.get(3)),
                createSurveillance(dateFormat.parse("2024-06-17 08:00"), dateFormat.parse("2024-06-17 10:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(9), matieres.get(9), sessions.get(3)),

                // Session 5
                createSurveillance(dateFormat.parse("2024-12-15 08:00"), dateFormat.parse("2024-12-15 10:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(0), matieres.get(0), sessions.get(4)),
                createSurveillance(dateFormat.parse("2024-12-15 10:30"), dateFormat.parse("2024-12-15 12:30"),
                        StatutSurveillance.PLANIFIEE, salles.get(1), matieres.get(1), sessions.get(4)),
                createSurveillance(dateFormat.parse("2024-12-16 08:00"), dateFormat.parse("2024-12-16 10:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(2), matieres.get(2), sessions.get(4)),
                createSurveillance(dateFormat.parse("2024-12-16 10:30"), dateFormat.parse("2024-12-16 12:30"),
                        StatutSurveillance.PLANIFIEE, salles.get(3), matieres.get(3), sessions.get(4)),
                createSurveillance(dateFormat.parse("2024-12-17 08:00"), dateFormat.parse("2024-12-17 10:00"),
                        StatutSurveillance.PLANIFIEE, salles.get(4), matieres.get(4), sessions.get(4))
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
                new Administrateur("Mohamed", "Ben Ali", "mohamed.benali@univ.edu", "AdminPass123", "20123456", "Directeur des examens"),
                new Administrateur("Fatima", "Zahra", "fatima.zahra@univ.edu", "AdminPass456", "23123456", "Responsable pédagogique"),
                new Administrateur("Karim", "Bouazizi", "karim.bouazizi@univ.edu", "AdminPass789", "25123456", "Chef de département Informatique"),
                new Administrateur("Leila", "Trabelsi", "leila.trabelsi@univ.edu", "AdminPass101", "27123456", "Responsable des salles"),
                new Administrateur("Ahmed", "Khalifa", "ahmed.khalifa@univ.edu", "AdminPass202", "29123456", "Coordinateur des examens"),
                new Administrateur("Samira", "Mejri", "samira.mejri@univ.edu", "AdminPass303", "30123456", "Secrétaire générale"),
                new Administrateur("Hichem", "Gharbi", "hichem.gharbi@univ.edu", "AdminPass404", "32123456", "Responsable RH"),
                new Administrateur("Amira", "Chaabane", "amira.chaabane@univ.edu", "AdminPass505", "34123456", "Responsable administratif"),
                new Administrateur("Youssef", "Mbarek", "youssef.mbarek@univ.edu", "AdminPass606", "36123456", "Vice-doyen"),
                new Administrateur("Salma", "Ben Youssef", "salma.benyoussef@univ.edu", "AdminPass707", "38123456", "Doyenne faculté des sciences")
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
                new Enseignant("Ali", "Ben Salem", "ali.bensalem@univ.edu", "ProfPass123", "50123456", "Professeur", "Informatique"),
                new Enseignant("Nadia", "Ben Ammar", "nadia.benammar@univ.edu", "ProfPass456", "51123456", "Maître de conférences", "Mathématiques"),
                new Enseignant("Rachid", "Gharsallah", "rachid.gharsallah@univ.edu", "ProfPass789", "52123456", "Professeur", "Physique"),
                new Enseignant("Houda", "Mansouri", "houda.mansouri@univ.edu", "ProfPass101", "53123456", "Maître de conférences", "Chimie"),
                new Enseignant("Sami", "Bouzid", "sami.bouzid@univ.edu", "ProfPass202", "54123456", "Professeur", "Biologie"),
                new Enseignant("Mouna", "Ben Ahmed", "mouna.benahmed@univ.edu", "ProfPass303", "55123456", "Maître de conférences", "Géologie"),
                new Enseignant("Tarek", "Chaari", "tarek.chaari@univ.edu", "ProfPass404", "56123456", "Professeur", "Informatique"),
                new Enseignant("Ines", "Ben Yedder", "ines.benyedder@univ.edu", "ProfPass505", "57123456", "Maître de conférences", "Mathématiques"),
                new Enseignant("Walid", "Saadi", "walid.saadi@univ.edu", "ProfPass606", "58123456", "Professeur", "Physique"),
                new Enseignant("Sonia", "Ben Amor", "sonia.benamor@univ.edu", "ProfPass707", "59123456", "Maître de conférences", "Chimie"),
                new Enseignant("Khalil", "Ben Brahim", "khalil.benbrahim@univ.edu", "ProfPass808", "60123456", "Professeur", "Informatique"),
                new Enseignant("Amina", "Ben Hassine", "amina.benhassine@univ.edu", "ProfPass909", "61123456", "Maître de conférences", "Mathématiques"),
                new Enseignant("Marwan", "Ben Abdallah", "marwan.benabdallah@univ.edu", "ProfPass1010", "62123456", "Professeur", "Physique"),
                new Enseignant("Selma", "Ben Miled", "selma.benmiled@univ.edu", "ProfPass1111", "63123456", "Maître de conférences", "Chimie"),
                new Enseignant("Adel", "Ben Hamida", "adel.benhamida@univ.edu", "ProfPass1212", "64123456", "Professeur", "Biologie")
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
                new AnneeUniversitaire(dateFormat.parse("2022-09-01"), dateFormat.parse("2023-06-30"), false),
                new AnneeUniversitaire(dateFormat.parse("2024-09-01"), dateFormat.parse("2025-06-30"), false),
                new AnneeUniversitaire(dateFormat.parse("2021-09-01"), dateFormat.parse("2022-06-30"), false),
                new AnneeUniversitaire(dateFormat.parse("2025-09-01"), dateFormat.parse("2026-06-30"), false)
        );

        for (AnneeUniversitaire annee : annees) {
            if (anneeUniversitaireRepository.findByDateDebutAndDateFin(annee.getDateDebut(), annee.getDateFin()).isEmpty()) {
                anneeUniversitaireRepository.save(annee);
            }
        }
    }

    private void insertMatieres() {
        List<Matiere> matieres = Arrays.asList(
                // Informatique
                new Matiere("L1", "Informatique", "INF101", "Algorithmique et programmation"),
                new Matiere("L1", "Informatique", "INF102", "Systèmes d'information"),
                new Matiere("L2", "Informatique", "INF201", "Structures de données"),
                new Matiere("L2", "Informatique", "INF202", "Bases de données"),
                new Matiere("L3", "Informatique", "INF301", "Réseaux informatiques"),
                new Matiere("L3", "Informatique", "INF302", "Intelligence artificielle"),

                // Mathématiques
                new Matiere("L1", "Mathématiques", "MAT101", "Algèbre linéaire"),
                new Matiere("L1", "Mathématiques", "MAT102", "Analyse réelle"),
                new Matiere("L2", "Mathématiques", "MAT201", "Probabilités"),
                new Matiere("L2", "Mathématiques", "MAT202", "Statistiques"),
                new Matiere("L3", "Mathématiques", "MAT301", "Analyse numérique"),
                new Matiere("L3", "Mathématiques", "MAT302", "Equations différentielles"),

                // Physique
                new Matiere("L1", "Physique", "PHY101", "Mécanique du point"),
                new Matiere("L1", "Physique", "PHY102", "Electromagnétisme"),
                new Matiere("L2", "Physique", "PHY201", "Thermodynamique"),
                new Matiere("L2", "Physique", "PHY202", "Optique"),
                new Matiere("L3", "Physique", "PHY301", "Mécanique quantique"),
                new Matiere("L3", "Physique", "PHY302", "Physique statistique"),

                // Chimie
                new Matiere("L1", "Chimie", "CHM101", "Chimie générale"),
                new Matiere("L1", "Chimie", "CHM102", "Chimie organique"),
                new Matiere("L2", "Chimie", "CHM201", "Chimie analytique"),
                new Matiere("L2", "Chimie", "CHM202", "Chimie physique"),
                new Matiere("L3", "Chimie", "CHM301", "Chimie des matériaux"),
                new Matiere("L3", "Chimie", "CHM302", "Chimie industrielle")
        );

        for (Matiere matiere : matieres) {
            if (matiereRepository.findByCode(matiere.getCode()).isEmpty()) {
                matiereRepository.save(matiere);
            }
        }
    }

    private void insertSalles() {
        List<Salle> salles = Arrays.asList(
                // Bâtiment A
                new Salle("A101", 50, "Bâtiment A", "1er étage"),
                new Salle("A102", 60, "Bâtiment A", "1er étage"),
                new Salle("A201", 70, "Bâtiment A", "2e étage"),
                new Salle("A202", 80, "Bâtiment A", "2e étage"),
                new Salle("A301", 90, "Bâtiment A", "3e étage"),

                // Bâtiment B
                new Salle("B101", 100, "Bâtiment B", "1er étage"),
                new Salle("B102", 110, "Bâtiment B", "1er étage"),
                new Salle("B201", 120, "Bâtiment B", "2e étage"),
                new Salle("B202", 130, "Bâtiment B", "2e étage"),
                new Salle("B301", 140, "Bâtiment B", "3e étage"),

                // Bâtiment C
                new Salle("C101", 150, "Bâtiment C", "1er étage"),
                new Salle("C102", 40, "Bâtiment C", "1er étage"),
                new Salle("C201", 50, "Bâtiment C", "2e étage"),
                new Salle("C202", 60, "Bâtiment C", "2e étage"),
                new Salle("C301", 70, "Bâtiment C", "3e étage"),

                // Bâtiment D
                new Salle("D101", 80, "Bâtiment D", "1er étage"),
                new Salle("D102", 90, "Bâtiment D", "1er étage"),
                new Salle("D201", 100, "Bâtiment D", "2e étage"),
                new Salle("D202", 110, "Bâtiment D", "2e étage"),
                new Salle("D301", 120, "Bâtiment D", "3e étage")
        );

        for (Salle salle : salles) {
            if (salleRepository.findByNumero(salle.getNumero()).isEmpty()) {
                salleRepository.save(salle);
            }
        }
    }

    private void insertSessionsExamen() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<AnneeUniversitaire> annees = anneeUniversitaireRepository.findAll();

        List<SessionExamen> sessions = Arrays.asList(
                // Année 2023-2024
                new SessionExamen(dateFormat.parse("2023-12-15"), dateFormat.parse("2023-12-22"),
                        TypeSession.PRINCIPALE, true, annees.get(0), Semestre.S1),
                new SessionExamen(dateFormat.parse("2024-01-15"), dateFormat.parse("2024-01-22"),
                        TypeSession.RATTRAPAGE, false, annees.get(0), Semestre.S1),
                new SessionExamen(dateFormat.parse("2024-05-15"), dateFormat.parse("2024-05-22"),
                        TypeSession.PRINCIPALE, true, annees.get(0), Semestre.S2),
                new SessionExamen(dateFormat.parse("2024-06-15"), dateFormat.parse("2024-06-22"),
                        TypeSession.RATTRAPAGE, false, annees.get(0), Semestre.S2),

                // Année 2022-2023
                new SessionExamen(dateFormat.parse("2022-12-15"), dateFormat.parse("2022-12-22"),
                        TypeSession.PRINCIPALE, false, annees.get(1), Semestre.S1),
                new SessionExamen(dateFormat.parse("2023-01-15"), dateFormat.parse("2023-01-22"),
                        TypeSession.RATTRAPAGE, false, annees.get(1), Semestre.S1),
                new SessionExamen(dateFormat.parse("2023-05-15"), dateFormat.parse("2023-05-22"),
                        TypeSession.PRINCIPALE, false, annees.get(1), Semestre.S2),
                new SessionExamen(dateFormat.parse("2023-06-15"), dateFormat.parse("2023-06-22"),
                        TypeSession.RATTRAPAGE, false, annees.get(1), Semestre.S2),

                // Année 2024-2025
                new SessionExamen(dateFormat.parse("2024-12-15"), dateFormat.parse("2024-12-22"),
                        TypeSession.PRINCIPALE, false, annees.get(2), Semestre.S1),
                new SessionExamen(dateFormat.parse("2025-01-15"), dateFormat.parse("2025-01-22"),
                        TypeSession.RATTRAPAGE, false, annees.get(2), Semestre.S1)
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
        List<AnneeUniversitaire> annees = anneeUniversitaireRepository.findAll();

        // Création de 50 relations enseigne
        List<Enseigne> enseignes = Arrays.asList(
                // Informatique
                new Enseigne(enseignants.get(0), matieres.get(0), Semestre.S1, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(0), matieres.get(0), Semestre.S1, annees.get(1), TypeMatiere.COURS),
                new Enseigne(enseignants.get(6), matieres.get(0), Semestre.S1, annees.get(0), TypeMatiere.TD),
                new Enseigne(enseignants.get(10), matieres.get(1), Semestre.S1, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(6), matieres.get(2), Semestre.S2, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(10), matieres.get(2), Semestre.S2, annees.get(0), TypeMatiere.TP),
                new Enseigne(enseignants.get(0), matieres.get(3), Semestre.S2, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(6), matieres.get(4), Semestre.S1, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(10), matieres.get(5), Semestre.S2, annees.get(0), TypeMatiere.COURS),

                // Mathématiques
                new Enseigne(enseignants.get(1), matieres.get(6), Semestre.S1, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(7), matieres.get(6), Semestre.S1, annees.get(0), TypeMatiere.TD),
                new Enseigne(enseignants.get(11), matieres.get(7), Semestre.S1, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(1), matieres.get(8), Semestre.S2, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(7), matieres.get(9), Semestre.S2, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(11), matieres.get(10), Semestre.S1, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(1), matieres.get(11), Semestre.S2, annees.get(0), TypeMatiere.COURS),

                // Physique
                new Enseigne(enseignants.get(2), matieres.get(12), Semestre.S1, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(8), matieres.get(12), Semestre.S1, annees.get(0), TypeMatiere.TP),
                new Enseigne(enseignants.get(12), matieres.get(13), Semestre.S1, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(2), matieres.get(14), Semestre.S2, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(8), matieres.get(15), Semestre.S2, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(12), matieres.get(16), Semestre.S1, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(2), matieres.get(17), Semestre.S2, annees.get(0), TypeMatiere.COURS),

                // Chimie
                new Enseigne(enseignants.get(3), matieres.get(18), Semestre.S1, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(9), matieres.get(18), Semestre.S1, annees.get(0), TypeMatiere.TP),
                new Enseigne(enseignants.get(13), matieres.get(19), Semestre.S1, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(3), matieres.get(20), Semestre.S2, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(9), matieres.get(21), Semestre.S2, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(13), matieres.get(22), Semestre.S1, annees.get(0), TypeMatiere.COURS),
                new Enseigne(enseignants.get(3), matieres.get(23), Semestre.S2, annees.get(0), TypeMatiere.COURS)
        );

        for (Enseigne enseigne : enseignes) {
            if (enseigneRepository.findByEnseignantAndMatiereAndAnnee(
                    enseigne.getEnseignant(), enseigne.getMatiere(), enseigne.getAnnee()).isEmpty()) {
                enseigneRepository.save(enseigne);
            }
        }
    }

    private void insertNotifications() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<Enseignant> enseignants = enseignantRepository.findAll();
        List<Surveillance> surveillances = surveillanceRepository.findAll();

        // Création de 50 notifications variées
        List<Notification> notifications = Arrays.asList(
                // Notifications d'affectation
                new Notification("Affectation à la surveillance de l'examen d'Algorithmique", dateFormat.parse("2023-12-10 09:00"), false, TypeNotification.AFFECTATION, enseignants.get(0), surveillances.get(0)),
                new Notification("Affectation à la surveillance de l'examen de Mathématiques", dateFormat.parse("2023-12-10 10:00"), false, TypeNotification.AFFECTATION, enseignants.get(1), surveillances.get(1)),
                new Notification("Affectation à la surveillance de l'examen de Physique", dateFormat.parse("2023-12-10 11:00"), false, TypeNotification.AFFECTATION, enseignants.get(2), surveillances.get(2)),
                new Notification("Affectation à la surveillance de l'examen de Chimie", dateFormat.parse("2023-12-10 12:00"), false, TypeNotification.AFFECTATION, enseignants.get(3), surveillances.get(3)),
                new Notification("Affectation à la surveillance de l'examen de Biologie", dateFormat.parse("2023-12-10 13:00"), false, TypeNotification.AFFECTATION, enseignants.get(4), surveillances.get(4)),

                // Rappels
                new Notification("Rappel: Surveillance demain à 08h00 - Salle A101", dateFormat.parse("2023-12-14 16:00"), false, TypeNotification.RAPPEL, enseignants.get(0), surveillances.get(0)),
                new Notification("Rappel: Surveillance demain à 10h30 - Salle B202", dateFormat.parse("2023-12-14 17:00"), false, TypeNotification.RAPPEL, enseignants.get(1), surveillances.get(1)),
                new Notification("Rappel: Surveillance demain à 14h00 - Salle C303", dateFormat.parse("2023-12-14 18:00"), false, TypeNotification.RAPPEL, enseignants.get(2), surveillances.get(2)),
                new Notification("Rappel: Surveillance demain à 08h00 - Salle D404", dateFormat.parse("2023-12-15 16:00"), false, TypeNotification.RAPPEL, enseignants.get(3), surveillances.get(3)),
                new Notification("Rappel: Surveillance demain à 10h30 - Salle E505", dateFormat.parse("2023-12-15 17:00"), false, TypeNotification.RAPPEL, enseignants.get(4), surveillances.get(4)),

                // Modifications
                new Notification("Changement de salle pour la surveillance de demain", dateFormat.parse("2023-12-14 15:00"), false, TypeNotification.MODIFICATION, enseignants.get(5), surveillances.get(5)),
                new Notification("Changement d'horaire pour la surveillance de vendredi", dateFormat.parse("2023-12-14 16:00"), false, TypeNotification.MODIFICATION, enseignants.get(6), surveillances.get(6)),
                new Notification("Annulation de la surveillance prévue", dateFormat.parse("2023-12-15 10:00"), false, TypeNotification.MODIFICATION, enseignants.get(7), surveillances.get(7)),
                new Notification("Nouvelle affectation suite à annulation", dateFormat.parse("2023-12-15 11:00"), false, TypeNotification.MODIFICATION, enseignants.get(8), surveillances.get(8)),
                new Notification("Modification des consignes de surveillance", dateFormat.parse("2023-12-15 12:00"), false, TypeNotification.MODIFICATION, enseignants.get(9), surveillances.get(9)),

                // Notifications générales
                new Notification("Réunion préparatoire des surveillants - Lundi 13/12 à 14h", dateFormat.parse("2023-12-10 14:00"), false, TypeNotification.AFFECTATION, enseignants.get(0), null),
                new Notification("Nouvelles consignes sanitaires pour les examens", dateFormat.parse("2023-12-11 09:00"), false, TypeNotification.RAPPEL, enseignants.get(1), null),
                new Notification("Distribution des feuilles d'émargement", dateFormat.parse("2023-12-12 10:00"), false, TypeNotification.AFFECTATION, enseignants.get(2), null),
                new Notification("Retour des copies d'examen", dateFormat.parse("2024-01-10 11:00"), false, TypeNotification.MODIFICATION, enseignants.get(3), null),
                new Notification("Bilan de la session d'examen", dateFormat.parse("2024-01-15 14:00"), false, TypeNotification.ANNULATION, enseignants.get(4), null)
        );

        for (Notification notification : notifications) {
            if (notificationRepository.findByMessageAndDateEnvoi(notification.getMessage(), notification.getDateEnvoi()).isEmpty()) {
                notificationRepository.save(notification);
            }
        }
    }
}