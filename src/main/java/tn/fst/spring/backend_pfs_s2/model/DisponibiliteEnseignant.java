package tn.fst.spring.backend_pfs_s2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class DisponibiliteEnseignant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "enseignant_id")
    @JsonIgnore
    private Enseignant enseignant;

    @ManyToOne
    @JoinColumn(name = "surveillance_id")
    @JsonIgnore
    private Surveillance surveillance;

    private Boolean estDisponible;
}