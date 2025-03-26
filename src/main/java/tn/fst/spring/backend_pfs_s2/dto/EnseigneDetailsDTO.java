package tn.fst.spring.backend_pfs_s2.dto;

import lombok.Data;

import java.util.Date;

@Data
public class EnseigneDetailsDTO {
    private Long id;

    // Enseignant Info
    private Long enseignantId;
    private String enseignantNom;
    private String enseignantPrenom;
    private String enseignantGrade;

    // Matiere Info
    private Long matiereId;
    private String matiereNom;
    private String matiereCode;

    // Annee Universitaire Info
    private Long anneeId;
    private Date anneeDateDebut;
    private Date anneeDateFin;

    private String numSemestre;
    private String typeMatiere;
}