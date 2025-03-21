package tn.fst.spring.backend_pfs_s2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Matiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String niveau;
    private String section;
    private String code;
    private String nom;

    @OneToMany(mappedBy = "matiere")
    private List<Enseigne> enseignes;

    // Constructeur par défaut
    public Matiere() {}

    // Constructeur avec paramètres
    public Matiere(String niveau, String section, String code, String nom) {
        this.niveau = niveau;
        this.section = section;
        this.code = code;
        this.nom = nom;
    }
}