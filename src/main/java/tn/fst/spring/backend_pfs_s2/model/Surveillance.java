package tn.fst.spring.backend_pfs_s2.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
public class Surveillance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date dateDebut;
    private Date dateFin;

    @Enumerated(EnumType.STRING)
    private StatutSurveillance statut;

    @Enumerated(EnumType.STRING)
    private TypeSurveillance type;

    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;

    @ManyToOne
    @JoinColumn(name = "matiere_id")
    private Matiere matiere;

    @ManyToOne
    @JoinColumn(name = "enseignant_principal_id")
    private Enseignant enseignantPrincipal;

    @ManyToOne
    @JoinColumn(name = "enseignant_secondaire_id")
    private Enseignant enseignantSecondaire;

    @ManyToOne
    @JoinColumn(name = "session_examen_id")
    private SessionExamen sessionExamen;

    @OneToMany(mappedBy = "surveillance")
    private List<Notification> notifications;
}