package tn.fst.spring.backend_pfs_s2.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}