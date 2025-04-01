package tn.fst.spring.backend_pfs_s2.dto;

import lombok.Data;

@Data
public class AdministrateurDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String fonction;
    private String motDePasse;
}