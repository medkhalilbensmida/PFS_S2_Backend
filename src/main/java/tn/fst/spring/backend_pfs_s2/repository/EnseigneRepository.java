package tn.fst.spring.backend_pfs_s2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import tn.fst.spring.backend_pfs_s2.model.Enseigne;
import tn.fst.spring.backend_pfs_s2.model.Matiere;

import java.util.List;

@Repository
public interface EnseigneRepository extends JpaRepository<Enseigne, Long> {
    List<Enseigne> findByEnseignantAndMatiere(Enseignant enseignant, Matiere matiere);

}
