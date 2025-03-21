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
}