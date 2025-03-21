package tn.fst.spring.backend_pfs_s2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
public class AnneeUniversitaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date dateDebut;
    private Date dateFin;
    private Boolean estActive;

    @OneToMany(mappedBy = "annee")
    private List<SessionExamen> sessions;

    // Constructeur par défaut
    public AnneeUniversitaire() {}

    // Constructeur avec paramètres
    public AnneeUniversitaire(Date dateDebut, Date dateFin, Boolean estActive) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.estActive = estActive;
    }
}