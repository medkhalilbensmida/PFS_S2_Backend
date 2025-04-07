package tn.fst.spring.backend_pfs_s2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;  // Importation de @Transactional
import tn.fst.spring.backend_pfs_s2.model.DisponibiliteEnseignant;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import tn.fst.spring.backend_pfs_s2.model.Surveillance;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisponibiliteEnseignantRepository extends JpaRepository<DisponibiliteEnseignant, Long> {

    List<DisponibiliteEnseignant> findByEnseignantId(Long enseignantId);

    List<DisponibiliteEnseignant> findBySurveillanceId(Long surveillanceId);

    Optional<DisponibiliteEnseignant> findByEnseignantAndSurveillance(Enseignant enseignant, Surveillance surveillance);

    boolean existsByEnseignantAndSurveillance(Enseignant enseignant, Surveillance surveillance);

    @Modifying
    @Transactional  // Gestion explicite de la transaction
    @Query("DELETE FROM DisponibiliteEnseignant d WHERE d.enseignant.id = :enseignantId")
    void deleteByEnseignantId(Long enseignantId);

    @Modifying
    @Transactional  // Gestion explicite de la transaction
    @Query("DELETE FROM DisponibiliteEnseignant d WHERE d.surveillance.id = :surveillanceId")
    void deleteBySurveillanceId(Long surveillanceId);

    @Modifying
    @Transactional  // Gestion explicite de la transaction
    @Query("UPDATE DisponibiliteEnseignant d SET d.estDisponible = :estDisponible WHERE d.id = :id")
    void updateDisponibilite(Long id, Boolean estDisponible);

    // Ajout de la gestion de la transaction pour l'insertion
    @Transactional  // Gestion explicite de la transaction
    default DisponibiliteEnseignant saveDisponibilite(DisponibiliteEnseignant disponibiliteEnseignant) {
        return save(disponibiliteEnseignant);
    }
}
