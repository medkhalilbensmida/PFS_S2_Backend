package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.model.Enseigne;
import tn.fst.spring.backend_pfs_s2.repository.EnseigneRepository;

import java.util.List;

@Service
public class EnseigneService {

    private final EnseigneRepository enseigneRepository;

    public EnseigneService(EnseigneRepository enseigneRepository) {
        this.enseigneRepository = enseigneRepository;
    }

    public List<Enseigne> getAllEnseignes() {
        return enseigneRepository.findAll();
    }

    public List<Enseigne> getEnseignesByEnseignantId(Long enseignantId) {
        return enseigneRepository.findByEnseignantId(enseignantId);
    }

    public Enseigne getEnseigneById(Long id) {
        return enseigneRepository.findById(id).orElse(null);
    }

    public Enseigne createEnseigne(Enseigne enseigne) {
        return enseigneRepository.save(enseigne);
    }

    public Enseigne updateEnseigne(Long id, Enseigne enseigne) {
        if (enseigneRepository.existsById(id)) {
            enseigne.setId(id);
            return enseigneRepository.save(enseigne);
        }
        return null;
    }

    public void deleteEnseigne(Long id) {
        enseigneRepository.deleteById(id);
    }
}