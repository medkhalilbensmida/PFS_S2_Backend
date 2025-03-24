package tn.fst.spring.backend_pfs_s2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.controller.AuthController;
import tn.fst.spring.backend_pfs_s2.model.Administrateur;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import tn.fst.spring.backend_pfs_s2.repository.AdministrateurRepository;
import tn.fst.spring.backend_pfs_s2.repository.EnseignantRepository;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdministrateurRepository administrateurRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private EnseignantRepository enseignantRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Loading user by email: " + email);
        // Rechercher dans Administrateur
        List<Administrateur> administrateurs = administrateurRepository.findByEmail(email);
        if (!administrateurs.isEmpty()) {
            return administrateurs.get(0); // Retourner le premier administrateur trouvé
        }

        // Rechercher dans Enseignant
        List<Enseignant> enseignants = enseignantRepository.findByEmail(email);
        if (!enseignants.isEmpty()) {
            return enseignants.get(0); // Retourner le premier enseignant trouvé
        }

        logger.error("User not found with email: " + email);
        // Si aucun utilisateur n'est trouvé
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}