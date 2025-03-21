package tn.fst.spring.backend_pfs_s2.dto;


import lombok.Data;

@Data
public class SalleDTO {
    private Long id;
    private String numero;
    private Integer capacite;
    private String batiment;
    private String etage;
}
