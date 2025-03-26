
package tn.fst.spring.backend_pfs_s2.service;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tn.fst.spring.backend_pfs_s2.model.Administrateur;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Long userId; // Ajout de l'ID utilisateur

    public CustomUserDetails(Administrateur admin) {
        this.email = admin.getEmail();
        this.password = admin.getMotDePasse();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        this.userId = admin.getId();
    }

    public CustomUserDetails(Enseignant enseignant) {
        this.email = enseignant.getEmail();
        this.password = enseignant.getMotDePasse();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ENSEIGNANT"));
        this.userId = enseignant.getId();
    }
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}