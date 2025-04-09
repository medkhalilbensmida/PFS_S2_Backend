package tn.fst.spring.backend_pfs_s2.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SessionExamenDetailsDTO {
    private Long id;
    private Date dateDebut;
    private Date dateFin;
    private String type;
    private Boolean estActive;
    private Long anneeUniversitaireId;
    private String numSemestre;
    private List<SurveillanceDetailsDTO> surveillances;
}
