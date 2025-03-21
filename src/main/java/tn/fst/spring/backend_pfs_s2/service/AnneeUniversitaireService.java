package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.model.AnneeUniversitaire;
import tn.fst.spring.backend_pfs_s2.repository.AnneeUniversitaireRepository;

import java.util.List;

@Service
public class AnneeUniversitaireService {

    @Autowired
    private AnneeUniversitaireRepository anneeUniversitaireRepository;

    public List<AnneeUniversitaire> getAllAnnees() {
        return anneeUniversitaireRepository.findAll();
    }

    public AnneeUniversitaire getAnneeById(Long id) {
        return anneeUniversitaireRepository.findById(id).orElse(null);
    }

    public AnneeUniversitaire createAnnee(AnneeUniversitaire anneeUniversitaire) {
        return anneeUniversitaireRepository.save(anneeUniversitaire);
    }

    public AnneeUniversitaire updateAnnee(Long id, AnneeUniversitaire anneeUniversitaire) {
        if (anneeUniversitaireRepository.existsById(id)) {
            anneeUniversitaire.setId(id);
            return anneeUniversitaireRepository.save(anneeUniversitaire);
        }
        return null;
    }

    public void deleteAnnee(Long id) {
        anneeUniversitaireRepository.deleteById(id);
    }
}