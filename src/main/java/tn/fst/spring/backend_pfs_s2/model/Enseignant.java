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
}