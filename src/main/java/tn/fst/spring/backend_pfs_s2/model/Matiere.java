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
}