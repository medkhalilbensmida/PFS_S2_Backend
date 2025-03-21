package tn.fst.spring.backend_pfs_s2.dto;


import lombok.Data;

import java.util.Date;

@Data
public class AnneeUniversitaireDTO {
    private Long id;
    private Date dateDebut;
    private Date dateFin;
    private Boolean estActive;
}