package tn.fst.spring.backend_pfs_s2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.backend_pfs_s2.model.SessionExamen;

import java.util.Date;
import java.util.List;

@Repository
public interface SessionExamenRepository extends JpaRepository<SessionExamen, Long> {
    List<SessionExamen> findByDateDebutAndDateFin(Date dateDebut, Date dateFin);

}