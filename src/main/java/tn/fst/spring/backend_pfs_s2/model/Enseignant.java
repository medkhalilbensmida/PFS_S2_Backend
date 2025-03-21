package tn.fst.spring.backend_pfs_s2.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class Enseignant extends Utilisateur {
    private String grade;
    private String departement;
    private Boolean estDisponible;

    @OneToMany(mappedBy = "enseignantPrincipal")
    private List<Surveillance> surveillancesPrincipales;

    @OneToMany(mappedBy = "enseignantSecondaire")
    private List<Surveillance> surveillancesSecondaires;

    @OneToMany(mappedBy = "enseignant")
    private List<Enseigne> enseignes;

    // Constructeur par défaut
    public Enseignant() {}

    // Constructeur avec paramètres
    public Enseignant(String nom, String prenom, String email, String motDePasse, String telephone, String grade, String departement, Boolean estDisponible) {
        super(nom, prenom, email, motDePasse, telephone);
        this.grade = grade;
        this.departement = departement;
        this.estDisponible = estDisponible;
    }
}