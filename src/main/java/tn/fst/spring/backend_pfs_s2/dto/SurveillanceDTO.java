package tn.fst.spring.backend_pfs_s2.dto;

import lombok.Data;
import tn.fst.spring.backend_pfs_s2.model.StatutSurveillance;

import java.util.Date;

@Data
public class SurveillanceDTO {
    private Long id;
    private Date dateDebut;
    private Date dateFin;
    private StatutSurveillance statut;
    private Long salleId;
    private Long matiereId;
    private Long enseignantPrincipalId;
    private Long enseignantSecondaireId;
    private Long sessionExamenId;
}