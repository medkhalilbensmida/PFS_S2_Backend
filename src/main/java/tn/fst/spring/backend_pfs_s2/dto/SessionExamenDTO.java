package tn.fst.spring.backend_pfs_s2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tn.fst.spring.backend_pfs_s2.model.TypeSession;

import java.util.Date;

@Data
public class SessionExamenDTO {
    private Long id; // Gardez l'ID dans le DTO pour la réponse

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date dateDebut;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date dateFin;

    private TypeSession type;
    private Boolean estActive;
    private Long anneeUniversitaireId;
    private String numSemestre;

    // Constructeur par défaut
    public SessionExamenDTO() {}

    // Constructeur avec tous les champs (optionnel)
    public SessionExamenDTO(Long id, Date dateDebut, Date dateFin, TypeSession type,
                            Boolean estActive, Long anneeUniversitaireId, String numSemestre) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.type = type;
        this.estActive = estActive;
        this.anneeUniversitaireId = anneeUniversitaireId;
        this.numSemestre = numSemestre;
    }
}