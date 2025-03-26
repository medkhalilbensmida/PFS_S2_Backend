package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.dto.EnseignantMatiereDTO;
import tn.fst.spring.backend_pfs_s2.model.*;
import tn.fst.spring.backend_pfs_s2.repository.EnseigneRepository;
import tn.fst.spring.backend_pfs_s2.repository.MatiereRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatiereService {

    private final MatiereRepository matiereRepository;
    private final EnseigneRepository enseigneRepository;

    @Autowired
    public MatiereService(MatiereRepository matiereRepository,
                          EnseigneRepository enseigneRepository) {
        this.matiereRepository = matiereRepository;
        this.enseigneRepository = enseigneRepository;
    }

    public List<Matiere> getAllMatieres() {
        return matiereRepository.findAll();
    }

    public List<EnseignantMatiereDTO> getMatieresDetailsByEnseignantId(Long enseignantId) {
        return enseigneRepository.findByEnseignantId(enseignantId).stream()
                .map(this::convertEnseigneToDTO)
                .collect(Collectors.toList());
    }

    private EnseignantMatiereDTO convertEnseigneToDTO(Enseigne enseigne) {
        EnseignantMatiereDTO dto = new EnseignantMatiereDTO();
        Matiere matiere = enseigne.getMatiere();

        dto.setId(matiere.getId());
        dto.setNiveau(matiere.getNiveau());
        dto.setSection(matiere.getSection());
        dto.setCode(matiere.getCode());
        dto.setNom(matiere.getNom());
        dto.setSemestre(enseigne.getNumSemestre());

        AnneeUniversitaire annee = enseigne.getAnnee();
        if (annee != null) {
            dto.setAnneeDebut(annee.getDateDebut());
            dto.setAnneeFin(annee.getDateFin());
            dto.setAnneeActive(annee.getEstActive());
        }

        return dto;
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