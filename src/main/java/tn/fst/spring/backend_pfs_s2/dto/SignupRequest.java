package tn.fst.spring.backend_pfs_s2.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
    private String role; // "ADMIN" ou "ENSEIGNANT"
    private String fonction; // Pour les administrateurs
    private String grade; // Pour les enseignants
    private String departement; // Pour les enseignants
    private Boolean estDisponible; // Pour les enseignants
}