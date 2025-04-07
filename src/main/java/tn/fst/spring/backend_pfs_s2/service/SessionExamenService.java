package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.fst.spring.backend_pfs_s2.model.AnneeUniversitaire;
import tn.fst.spring.backend_pfs_s2.model.SessionExamen;
import tn.fst.spring.backend_pfs_s2.repository.AnneeUniversitaireRepository;
import tn.fst.spring.backend_pfs_s2.repository.SessionExamenRepository;
import tn.fst.spring.backend_pfs_s2.repository.SurveillanceRepository;

import java.util.List;

@Service
public class SessionExamenService {

    @Autowired
    private SessionExamenRepository sessionExamenRepository;

    @Autowired
    private SurveillanceRepository surveillanceRepository;

    @Autowired
    private AnneeUniversitaireRepository anneeUniversitaireRepository;

    public List<SessionExamen> getAllSessions() {
        return sessionExamenRepository.findAll();
    }

    public SessionExamen getSessionById(Long id) {
        return sessionExamenRepository.findById(id).orElse(null);
    }

    @Transactional
    public SessionExamen createSession(SessionExamen sessionExamen) {
        if (sessionExamen.getId() != null) {
            throw new IllegalArgumentException("L'ID ne doit pas être fourni lors de la création");
        }

        if (sessionExamen.getAnnee() == null || sessionExamen.getAnnee().getId() == null) {
            throw new IllegalArgumentException("L'année universitaire doit être spécifiée");
        }

        AnneeUniversitaire annee = anneeUniversitaireRepository.findById(sessionExamen.getAnnee().getId())
                .orElseThrow(() -> new IllegalArgumentException("L'année universitaire spécifiée n'existe pas"));

        sessionExamen.setAnnee(annee);

        if (sessionExamen.getDateDebut() == null || sessionExamen.getDateFin() == null) {
            throw new IllegalArgumentException("Les dates de début et fin doivent être spécifiées");
        }

        if (sessionExamen.getDateDebut().after(sessionExamen.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        return sessionExamenRepository.save(sessionExamen);
    }

    @Transactional
    public SessionExamen updateSession(Long id, SessionExamen sessionExamen) {
        SessionExamen existingSession = sessionExamenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session non trouvée avec l'ID: " + id));

        // Mettre à jour les champs un par un au lieu de remplacer l'entité
        if (sessionExamen.getAnnee() == null || sessionExamen.getAnnee().getId() == null) {
            throw new IllegalArgumentException("L'année universitaire doit être spécifiée");
        }

        AnneeUniversitaire annee = anneeUniversitaireRepository.findById(sessionExamen.getAnnee().getId())
                .orElseThrow(() -> new IllegalArgumentException("L'année universitaire avec l'ID " + sessionExamen.getAnnee().getId() + " n'existe pas"));

        existingSession.setAnnee(annee);
        existingSession.setDateDebut(sessionExamen.getDateDebut());
        existingSession.setDateFin(sessionExamen.getDateFin());
        existingSession.setType(sessionExamen.getType());
        existingSession.setEstActive(sessionExamen.getEstActive());
        existingSession.setNumSemestre(sessionExamen.getNumSemestre());

        if (existingSession.getDateDebut().after(existingSession.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }

        return sessionExamenRepository.save(existingSession);
    }

    @Transactional
    public void deleteSession(Long id) {
        SessionExamen session = sessionExamenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session non trouvée avec l'ID: " + id));

        if (!session.getSurveillances().isEmpty()) {
            throw new DataIntegrityViolationException(
                    "Impossible de supprimer la session car elle est liée à des surveillances"
            );
        }

        sessionExamenRepository.delete(session);
    }
}