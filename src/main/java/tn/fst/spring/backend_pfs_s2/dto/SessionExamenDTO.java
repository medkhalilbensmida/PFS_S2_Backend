package tn.fst.spring.backend_pfs_s2.dto;

import lombok.Data;
import tn.fst.spring.backend_pfs_s2.model.TypeSession;

import java.util.Date;

@Data
public class SessionExamenDTO {
    private Long id;
    private Date dateDebut;
    private Date dateFin;
    private TypeSession type;
    private Boolean estActive;
    private Long anneeUniversitaireId;
    private String numSemestre;
}

