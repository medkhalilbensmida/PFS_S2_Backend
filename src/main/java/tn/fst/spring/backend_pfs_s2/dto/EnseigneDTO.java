package tn.fst.spring.backend_pfs_s2.dto;

import lombok.Data;
import tn.fst.spring.backend_pfs_s2.model.TypeMatiere;

@Data
public class EnseigneDTO {
    private Long id;
    private Long enseignantId;
    private Long matiereId;
    private String numSemestre;
    private Long anneeUniversitaireId;
    private TypeMatiere typeMatiere;
}