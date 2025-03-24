package tn.fst.spring.backend_pfs_s2.dto;

import lombok.Data;

@Data
public class DisponibiliteEnseignantDTO {
    private Long id;
    private Long enseignantId;
    private Long surveillanceId;
    private Boolean estDisponible;
}