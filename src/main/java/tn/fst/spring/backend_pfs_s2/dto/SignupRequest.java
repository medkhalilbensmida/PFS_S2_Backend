package tn.fst.spring.backend_pfs_s2.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
    private String role;

    // Champs spécifiques à l'administrateur
    private String fonction;

    // Champs spécifiques à l'enseignant
    private String grade;
    private String departement;
}