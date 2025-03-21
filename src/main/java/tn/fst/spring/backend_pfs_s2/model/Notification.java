package tn.fst.spring.backend_pfs_s2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private Date dateEnvoi;
    private Boolean estLue;

    @Enumerated(EnumType.STRING)
    private TypeNotification type;

    @ManyToOne
    @JoinColumn(name = "enseignant_id")
    private Enseignant destinataire;

    @ManyToOne
    @JoinColumn(name = "surveillance_id")
    private Surveillance surveillance;

    // Constructeur par défaut
    public Notification() {}

    // Constructeur avec paramètres
    public Notification(String message, Date dateEnvoi, Boolean estLue, TypeNotification type, Enseignant destinataire, Surveillance surveillance) {
        this.message = message;
        this.dateEnvoi = dateEnvoi;
        this.estLue = estLue;
        this.type = type;
        this.destinataire = destinataire;
        this.surveillance = surveillance;
    }
}