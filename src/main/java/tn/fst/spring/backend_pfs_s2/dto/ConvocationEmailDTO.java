package tn.fst.spring.backend_pfs_s2.dto;

import lombok.Data;
import tn.fst.spring.backend_pfs_s2.model.Semestre;
import tn.fst.spring.backend_pfs_s2.model.TypeSession;

@Data
public class ConvocationEmailDTO extends NotificationEmailDTO{
    private String anneeUniversitaire;
    private Semestre semestre;
    private TypeSession typeSession;
}
