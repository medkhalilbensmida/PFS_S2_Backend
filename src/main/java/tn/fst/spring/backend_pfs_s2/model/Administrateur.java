package tn.fst.spring.backend_pfs_s2.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class Administrateur extends Utilisateur {
    private String fonction;
    private String signature;

    public Administrateur() {}

    public Administrateur(String nom, String prenom, String email, String motDePasse, String telephone, String fonction) {
        super(nom, prenom, email, motDePasse, telephone);
        this.fonction = fonction;
    }
}