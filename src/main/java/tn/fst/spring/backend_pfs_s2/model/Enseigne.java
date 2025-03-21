package tn.fst.spring.backend_pfs_s2.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Enseigne {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "enseignant_id")
    private Enseignant enseignant;

    @ManyToOne
    @JoinColumn(name = "matiere_id")
    private Matiere matiere;

    @Enumerated(EnumType.STRING)
    private Semestre numSemestre;

    @ManyToOne
    @JoinColumn(name = "annee_universitaire_id")
    private AnneeUniversitaire annee;

    @Enumerated(EnumType.STRING)
    private TypeMatiere typeMatiere;
}