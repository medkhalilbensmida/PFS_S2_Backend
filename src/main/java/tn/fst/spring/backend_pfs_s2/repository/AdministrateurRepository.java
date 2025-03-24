package tn.fst.spring.backend_pfs_s2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.backend_pfs_s2.model.Administrateur;

import java.util.List;

@Repository
public interface AdministrateurRepository extends JpaRepository<Administrateur, Long> {
    List<Administrateur> findByEmail(String email);
    boolean existsByEmail(String email); // Add this method
}
