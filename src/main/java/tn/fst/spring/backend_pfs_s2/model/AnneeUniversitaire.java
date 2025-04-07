package tn.fst.spring.backend_pfs_s2.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "annee_universitaire")
public class AnneeUniversitaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateDebut;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateFin;

    @Column(nullable = false)
    private Boolean estActive;

    @OneToMany(mappedBy = "annee", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<SessionExamen> sessions;

    public AnneeUniversitaire() {}

    public AnneeUniversitaire(Long id) {
        this.id = id;
    }

    public AnneeUniversitaire(Date dateDebut, Date dateFin, Boolean estActive) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.estActive = estActive;
    }

    @Override
    public String toString() {
        return "" +
                (dateDebut.getYear() + 1900) +
                "-" +
                (dateFin.getYear() + 1900) +
                "";
    }
}