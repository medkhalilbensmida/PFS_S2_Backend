package tn.fst.spring.backend_pfs_s2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.fst.spring.backend_pfs_s2.model.Surveillance;

import java.util.Date;
import java.util.List;

@Repository
public interface SurveillanceRepository extends JpaRepository<Surveillance, Long> {

    // Existing methods
    List<Surveillance> findByDateDebutAndDateFin(Date dateDebut, Date dateFin);
    boolean existsByDateDebutAndDateFin(Date dateDebut, Date dateFin);

    @Query("SELECT COUNT(s) > 0 FROM Surveillance s WHERE s.sessionExamen.id = :sessionId")
    boolean existsBySessionExamenId(@Param("sessionId") Long sessionId);

    // New methods for better session-surveillance management
    List<Surveillance> findBySessionExamenId(Long sessionId);

    @Query("SELECT COUNT(s) > 0 FROM Surveillance s " +
            "WHERE s.sessionExamen.id = :sessionId " +
            "AND EXISTS (SELECT 1 FROM Notification n WHERE n.surveillance = s)")
    boolean existsBySessionExamenIdWithNotifications(@Param("sessionId") Long sessionId);

    @Modifying
    @Query("UPDATE Surveillance s SET " +
            "s.dateDebut = :newDateDebut, " +
            "s.dateFin = :newDateFin " +
            "WHERE s.sessionExamen.id = :sessionId")
    int updateSurveillanceDatesForSession(
            @Param("sessionId") Long sessionId,
            @Param("newDateDebut") Date newDateDebut,
            @Param("newDateFin") Date newDateFin);

    @Query("SELECT s FROM Surveillance s WHERE " +
            "s.sessionExamen.id = :sessionId " +
            "AND (s.dateDebut > :newDateFin OR s.dateFin < :newDateDebut)")
    List<Surveillance> findSurveillancesOutsideSessionDates(
            @Param("sessionId") Long sessionId,
            @Param("newDateDebut") Date newDateDebut,
            @Param("newDateFin") Date newDateFin);

    @Query("SELECT COUNT(s) > 0 FROM Surveillance s WHERE " +
            "s.salle.id = :salleId " +
            "AND s.id <> :excludeSurveillanceId " +
            "AND (s.dateDebut < :dateFin AND s.dateFin > :dateDebut)")
    boolean existsOverlappingSurveillanceForSalle(
            @Param("salleId") Long salleId,
            @Param("dateDebut") Date dateDebut,
            @Param("dateFin") Date dateFin,
            @Param("excludeSurveillanceId") Long excludeSurveillanceId);

    // Find surveillances where the given enseignant is either principal or secondaire
    List<Surveillance> findByEnseignantPrincipalIdOrEnseignantSecondaireId(Long enseignantPrincipalId, Long enseignantSecondaireId);

    // Custom query to find surveillances that overlap with a given time range for a specific enseignant,
    // excluding a specific surveillance ID (useful for checking conflicts when updating/assigning)
    @Query("SELECT s FROM Surveillance s WHERE s.id <> :excludeSurveillanceId AND " +
           "((s.enseignantPrincipal.id = :enseignantId) OR (s.enseignantSecondaire.id = :enseignantId)) AND " +
           "((s.dateDebut < :endDate AND s.dateFin > :startDate))") // Overlap condition
    List<Surveillance> findOverlappingSurveillancesForEnseignant(
            @Param("enseignantId") Long enseignantId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("excludeSurveillanceId") Long excludeSurveillanceId
    );
}