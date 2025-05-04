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


    private Boolean emailEnvoye = false; 

    // Constructeur par défaut
    public Notification() {}

    // Constructeur avec paramètres
    public Notification(String message, Date dateEnvoi, Boolean estLue, TypeNotification type, Enseignant enseignant, Surveillance surveillance) {
        this.message = message;
        this.dateEnvoi = dateEnvoi;
        this.estLue = estLue;
        this.type = type;
        this.enseignantDestinataire = enseignant;
        this.emailEnvoye = false;
    }


    public Notification(String message, Date dateEnvoi, Boolean estLue, TypeNotification type, Administrateur admin, Surveillance surveillance) {
        this.message = message;
        this.dateEnvoi = dateEnvoi;
        this.estLue = estLue;
        this.type = type;
        this.adminDestinataire = admin;
        this.emailEnvoye = false;
    }


    // Method to mark email as sent
    public void markEmailAsSent() {
        this.emailEnvoye = true;
    }

    @PrePersist
    protected void onCreate() {
        this.dateEnvoi = new Date();
    }

    @ManyToOne
    @JoinColumn(name = "enseignant_id")
    private Enseignant enseignantDestinataire;
    
    @ManyToOne
    @JoinColumn(name = "administrateur_id")
    private Administrateur adminDestinataire;
    
}