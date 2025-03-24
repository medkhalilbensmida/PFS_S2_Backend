package tn.fst.spring.backend_pfs_s2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @OneToMany(mappedBy = "surveillance")
    @JsonIgnore
    private List<DisponibiliteEnseignant> disponibilitesEnseignants;

    @Enumerated(EnumType.STRING)
    private StatutSurveillance statut;

    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;

    @ManyToOne
    @JoinColumn(name = "matiere_id")
    private Matiere matiere;

    @ManyToOne
    @JoinColumn(name = "session_examen_id")
    private SessionExamen sessionExamen;

    @ManyToOne
    @JoinColumn(name = "enseignant_principal_id")
    private Enseignant enseignantPrincipal;

    @ManyToOne
    @JoinColumn(name = "enseignant_secondaire_id")
    private Enseignant enseignantSecondaire;

    @OneToMany(mappedBy = "surveillance")
    private List<Notification> notifications;

    public Surveillance() {}

    // Constructeur pour DataInitializer
    public Surveillance(Date dateDebut, Date dateFin, StatutSurveillance statut,
                        Salle salle, Matiere matiere, SessionExamen sessionExamen) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.salle = salle;
        this.matiere = matiere;
        this.sessionExamen = sessionExamen;
    }
}