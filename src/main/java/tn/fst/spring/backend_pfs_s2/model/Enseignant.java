package tn.fst.spring.backend_pfs_s2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @OneToMany(mappedBy = "enseignant")
    @JsonIgnore
    private List<DisponibiliteEnseignant> disponibilites;

    @OneToMany(mappedBy = "enseignantPrincipal")
    @JsonIgnore
    private List<Surveillance> surveillancesPrincipales;

    @OneToMany(mappedBy = "enseignantSecondaire")
    @JsonIgnore
    private List<Surveillance> surveillancesSecondaires;

    @OneToMany(mappedBy = "enseignant")
    private List<Enseigne> enseignes;

    public Enseignant() {}

    public Enseignant(String nom, String prenom, String email, String motDePasse,
                      String telephone, String grade, String departement) {
        super(nom, prenom, email, motDePasse, telephone);
        this.grade = grade;
        this.departement = departement;
    }
}