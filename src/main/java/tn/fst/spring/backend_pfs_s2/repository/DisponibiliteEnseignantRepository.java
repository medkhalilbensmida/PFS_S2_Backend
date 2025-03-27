package tn.fst.spring.backend_pfs_s2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
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
}
