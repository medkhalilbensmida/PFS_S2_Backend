package tn.fst.spring.backend_pfs_s2.dto;

import lombok.Data;
import tn.fst.spring.backend_pfs_s2.model.Semestre;

import java.util.Date;

@Data
public class EnseignantMatiereDTO {
    private Long id;
    private String niveau;
    private String section;
    private String code;
    private String nom;
    private Semestre semestre;
    private Date anneeDebut;
    private Date anneeFin;
    private boolean anneeActive;
}