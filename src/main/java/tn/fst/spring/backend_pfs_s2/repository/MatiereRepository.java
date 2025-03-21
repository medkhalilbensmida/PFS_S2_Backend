package tn.fst.spring.backend_pfs_s2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.backend_pfs_s2.model.Matiere;

@Repository
public interface MatiereRepository extends JpaRepository<Matiere, Long> {
}