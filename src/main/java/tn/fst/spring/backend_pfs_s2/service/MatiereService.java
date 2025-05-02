package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.dto.EnseignantMatiereDTO;
import tn.fst.spring.backend_pfs_s2.dto.MatiereDTO;
import tn.fst.spring.backend_pfs_s2.dto.SectionDTO;
import tn.fst.spring.backend_pfs_s2.exception.ResourceNotFoundException;
import tn.fst.spring.backend_pfs_s2.model.*;
import tn.fst.spring.backend_pfs_s2.repository.EnseigneRepository;
import tn.fst.spring.backend_pfs_s2.repository.MatiereRepository;
import tn.fst.spring.backend_pfs_s2.repository.SectionRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MatiereService {

    private final MatiereRepository matiereRepository;
    private final EnseigneRepository enseigneRepository;
    private final SectionRepository sectionRepository;

    @Autowired
    public MatiereService(MatiereRepository matiereRepository,
                          EnseigneRepository enseigneRepository,
                          SectionRepository sectionRepository) {
        this.matiereRepository = matiereRepository;
        this.enseigneRepository = enseigneRepository;
        this.sectionRepository = sectionRepository;
    }

    private MatiereDTO convertToMatiereDTO(Matiere matiere) {
        MatiereDTO dto = new MatiereDTO();
        dto.setId(matiere.getId());
        dto.setNiveau(matiere.getNiveau());
        dto.setCode(matiere.getCode());
        dto.setNom(matiere.getNom());
        if (matiere.getSection() != null) {
            dto.setSection(new SectionDTO(matiere.getSection().getName(), matiere.getSection().getStudentNumber()));
        }
        return dto;
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
        if (matiere.getSection() != null) {
            dto.setSection(matiere.getSection().getName());
        }
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

    public List<MatiereDTO> findMatieresBySectionName(String sectionName) {
        Section section = sectionRepository.findById(sectionName)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with name: " + sectionName));
        List<Matiere> matieres = matiereRepository.findBySection(section);
        return matieres.stream()
                       .map(this::convertToMatiereDTO)
                       .collect(Collectors.toList());
    }

    public Map<String, List<MatiereDTO>> findAllMatieresGroupedBySectionName() {
        List<Matiere> allMatieres = matiereRepository.findAll();
        return allMatieres.stream()
                          .filter(m -> m.getSection() != null)
                          .collect(Collectors.groupingBy(
                                  m -> m.getSection().getName(),
                                  Collectors.mapping(this::convertToMatiereDTO, Collectors.toList())
                          ));
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