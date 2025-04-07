package tn.fst.spring.backend_pfs_s2.dto;

import lombok.Data;

@Data
public class EnseignantDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String grade;
    private String departement;
    private String motDePasse;
    private String photoProfil;
}