package tn.fst.spring.backend_pfs_s2.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String jwt;

    public AuthResponse(String jwt) {
        this.jwt = jwt;
    }
}