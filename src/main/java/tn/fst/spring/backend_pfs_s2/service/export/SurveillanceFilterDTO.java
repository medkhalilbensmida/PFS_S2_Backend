package tn.fst.spring.backend_pfs_s2.service.export;


import lombok.Data;
import tn.fst.spring.backend_pfs_s2.model.Semestre;
import tn.fst.spring.backend_pfs_s2.model.TypeSession;

@Data
public class SurveillanceFilterDTO {
    private String anneeUniversitaire;
    private Semestre semestre;
    private TypeSession typeSession;
}
