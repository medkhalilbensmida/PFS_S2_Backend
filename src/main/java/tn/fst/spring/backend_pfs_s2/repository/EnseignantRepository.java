package tn.fst.spring.backend_pfs_s2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;

import java.util.Optional;

@Repository
public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {
    Optional<Enseignant> findByEmail(String email);
    boolean existsByEmail(String email);
}