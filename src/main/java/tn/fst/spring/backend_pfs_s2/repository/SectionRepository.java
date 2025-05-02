package tn.fst.spring.backend_pfs_s2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.backend_pfs_s2.model.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, String> {
    // Add custom query methods here if needed in the future
} 