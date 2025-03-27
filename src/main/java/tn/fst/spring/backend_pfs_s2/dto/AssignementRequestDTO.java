package tn.fst.spring.backend_pfs_s2.dto;

import lombok.Data;

@Data
public class AssignementRequestDTO {
    private Long enseignantPrincipalId; // Peut être null pour désaffecter
    private Long enseignantSecondaireId; // Peut être null pour désaffecter
}