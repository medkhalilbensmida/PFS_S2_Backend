package tn.fst.spring.backend_pfs_s2.model;


import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class Administrateur extends Utilisateur {
    private String fonction;

    @OneToMany(mappedBy = "administrateur")
    private List<SessionExamen> sessions;
}
