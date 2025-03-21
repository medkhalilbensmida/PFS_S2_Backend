package tn.fst.spring.backend_pfs_s2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Salle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;
    private Integer capacite;
    private String batiment;
    private String etage;

    @OneToMany(mappedBy = "salle")
    private List<Surveillance> surveillances;
}