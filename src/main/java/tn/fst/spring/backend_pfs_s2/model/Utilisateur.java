package tn.fst.spring.backend_pfs_s2.model;
import jakarta.persistence.*;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
}