package tn.fst.spring.backend_pfs_s2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
public class SessionExamen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date dateDebut;
    private Date dateFin;

    @Enumerated(EnumType.STRING)
    private TypeSession type;

    private Boolean estActive;

    @ManyToOne
    @JoinColumn(name = "annee_universitaire_id")
    private AnneeUniversitaire annee;

    @Enumerated(EnumType.STRING)
    private Semestre numSemestre;

    @OneToMany(mappedBy = "sessionExamen")
    private List<Surveillance> surveillances;

    // Constructeur par défaut
    public SessionExamen() {}

    // Constructeur avec paramètres
    public SessionExamen(Date dateDebut, Date dateFin, TypeSession type, Boolean estActive, AnneeUniversitaire annee, Semestre numSemestre) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.type = type;
        this.estActive = estActive;
        this.annee = annee;
        this.numSemestre = numSemestre;
    }
}