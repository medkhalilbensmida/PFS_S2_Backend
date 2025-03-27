package tn.fst.spring.backend_pfs_s2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.fst.spring.backend_pfs_s2.model.Surveillance;

import java.util.Date;
import java.util.List;

@Repository
public interface SurveillanceRepository extends JpaRepository<Surveillance, Long> {
    List<Surveillance> findByDateDebutAndDateFin(Date dateDebut, Date dateFin);
    boolean existsByDateDebutAndDateFin(Date dateDebut, Date dateFin); // Add this method

    /**
     * Trouve les surveillances qui chevauchent une période donnée pour un enseignant spécifique,
     * excluant une surveillance spécifique (utile pour vérifier les conflits lors de la mise à jour).
     * @param enseignantId L'ID de l'enseignant
     * @param dateDebut La date de début de la période de la nouvelle surveillance
     * @param dateFin La date de fin de la période de la nouvelle surveillance
     * @param excludeSurveillanceId L'ID de la surveillance actuelle qu'on essaie d'affecter (pour l'exclure de la vérification)
     * @return Une liste de surveillances conflictuelles
     */
    @Query("SELECT s FROM Surveillance s WHERE " +
            "(s.enseignantPrincipal.id = :enseignantId OR s.enseignantSecondaire.id = :enseignantId) " +
            "AND s.id <> :excludeSurveillanceId " + // Exclure la surveillance en cours d'affectation
            "AND (s.dateDebut < :dateFin AND s.dateFin > :dateDebut)") // Logique de chevauchement de temps
    List<Surveillance> findOverlappingSurveillancesForEnseignant(
            @Param("enseignantId") Long enseignantId,
            @Param("dateDebut") Date dateDebut,
            @Param("dateFin") Date dateFin,
            @Param("excludeSurveillanceId") Long excludeSurveillanceId
    );
}