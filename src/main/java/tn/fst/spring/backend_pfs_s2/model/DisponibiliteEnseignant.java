package tn.fst.spring.backend_pfs_s2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "disponibilite_enseignant")
public class DisponibiliteEnseignant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enseignant_id", nullable = false)
    @JsonIgnore
    private Enseignant enseignant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surveillance_id", nullable = false)
    @JsonIgnore
    private Surveillance surveillance;

    @Column(name = "est_disponible", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean estDisponible = false;

    @PrePersist
    public void prePersist() {
        if (estDisponible == null) {
            estDisponible = false;
        }
    }
}