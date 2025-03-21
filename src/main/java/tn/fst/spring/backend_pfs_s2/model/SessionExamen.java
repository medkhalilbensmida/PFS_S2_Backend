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

    @ManyToOne
    @JoinColumn(name = "administrateur_id")
    private Administrateur administrateur;

    @OneToMany(mappedBy = "sessionExamen")
    private List<Surveillance> surveillances;
}