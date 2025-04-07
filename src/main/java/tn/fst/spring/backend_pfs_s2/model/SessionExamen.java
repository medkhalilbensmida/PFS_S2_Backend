package tn.fst.spring.backend_pfs_s2.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "session_examen")
public class SessionExamen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Supprimez toute annotation @GeneratedValue si elle existe

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dateDebut;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeSession type;

    @Column(nullable = false)
    private Boolean estActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_universitaire_id", nullable = false)
    private AnneeUniversitaire annee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Semestre numSemestre;

    @OneToMany(mappedBy = "sessionExamen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Surveillance> surveillances;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    public SessionExamen() {}

    // Constructeur sans ID
    public SessionExamen(Date dateDebut, Date dateFin, TypeSession type,
                         Boolean estActive, AnneeUniversitaire annee,
                         Semestre numSemestre) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.type = type;
        this.estActive = estActive;
        this.annee = annee;
        this.numSemestre = numSemestre;
    }
}