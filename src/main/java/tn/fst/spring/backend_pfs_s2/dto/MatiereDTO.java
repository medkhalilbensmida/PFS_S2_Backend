package tn.fst.spring.backend_pfs_s2.dto;


import lombok.Data;

@Data
public class MatiereDTO {
    private Long id;
    private String niveau;
    private String section;
    private String code;
    private String nom;
}