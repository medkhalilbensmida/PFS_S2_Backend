package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.fst.spring.backend_pfs_s2.model.Administrateur;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import tn.fst.spring.backend_pfs_s2.repository.AdministrateurRepository;
import tn.fst.spring.backend_pfs_s2.repository.EnseignantRepository;

import java.util.List;
import java.util.Optional;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdministrateurRepository administrateurRepository;
    private final EnseignantRepository enseignantRepository;

    public CustomUserDetailsService(AdministrateurRepository administrateurRepository,
                                    EnseignantRepository enseignantRepository) {
        this.administrateurRepository = administrateurRepository;
        this.enseignantRepository = enseignantRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Recherche dans Administrateur
        Optional<Administrateur> admins = administrateurRepository.findByEmail(username);
        if (!admins.isEmpty()) {
            return new CustomUserDetails(admins.get());
        }

        // Recherche dans Enseignant
        Optional<Enseignant> enseignants = enseignantRepository.findByEmail(username);
        if (!enseignants.isEmpty()) {
            return new CustomUserDetails(enseignants.get());
        }

        throw new UsernameNotFoundException("User not found with email: " + username);
    }
}