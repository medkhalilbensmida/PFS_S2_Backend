package tn.fst.spring.backend_pfs_s2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.backend_pfs_s2.model.AnneeUniversitaire;

import java.util.Date;
import java.util.List;

@Repository
public interface AnneeUniversitaireRepository extends JpaRepository<AnneeUniversitaire, Long> {
    List<AnneeUniversitaire> findByDateDebutAndDateFin(Date dateDebut, Date dateFin);

}