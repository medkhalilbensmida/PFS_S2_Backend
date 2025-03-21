package tn.fst.spring.backend_pfs_s2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.backend_pfs_s2.model.Enseigne;

@Repository
public interface EnseigneRepository extends JpaRepository<Enseigne, Long> {
}
