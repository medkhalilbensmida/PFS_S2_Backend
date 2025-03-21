package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.model.Matiere;
import tn.fst.spring.backend_pfs_s2.repository.MatiereRepository;

import java.util.List;

@Service
public class MatiereService {

    @Autowired
    private MatiereRepository matiereRepository;

    public List<Matiere> getAllMatieres() {
        return matiereRepository.findAll();
    }

    public Matiere getMatiereById(Long id) {
        return matiereRepository.findById(id).orElse(null);
    }

    public Matiere createMatiere(Matiere matiere) {
        return matiereRepository.save(matiere);
    }

    public Matiere updateMatiere(Long id, Matiere matiere) {
        if (matiereRepository.existsById(id)) {
            matiere.setId(id);
            return matiereRepository.save(matiere);
        }
        return null;
    }

    public void deleteMatiere(Long id) {
        matiereRepository.deleteById(id);
    }
}
