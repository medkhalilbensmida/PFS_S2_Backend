package tn.fst.spring.backend_pfs_s2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseAdministrateur {
    private String token;
    private String role;
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String fonction;
}