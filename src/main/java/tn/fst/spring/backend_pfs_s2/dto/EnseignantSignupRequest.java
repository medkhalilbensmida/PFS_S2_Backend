package tn.fst.spring.backend_pfs_s2.dto;

import lombok.Data;

@Data
public class EnseignantSignupRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
    private String grade;
    private String departement;
}