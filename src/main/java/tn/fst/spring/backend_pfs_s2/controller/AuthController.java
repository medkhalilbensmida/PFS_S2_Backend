package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.*;
import tn.fst.spring.backend_pfs_s2.model.Administrateur;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import tn.fst.spring.backend_pfs_s2.repository.AdministrateurRepository;
import tn.fst.spring.backend_pfs_s2.repository.EnseignantRepository;
import tn.fst.spring.backend_pfs_s2.service.JwtService;
import tn.fst.spring.backend_pfs_s2.service.CustomUserDetailsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API pour l'authentification")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AdministrateurRepository administrateurRepository;
    private final EnseignantRepository enseignantRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          CustomUserDetailsService userDetailsService,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder,
                          AdministrateurRepository administrateurRepository,
                          EnseignantRepository enseignantRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.administrateurRepository = administrateurRepository;
        this.enseignantRepository = enseignantRepository;
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur et retourne un JWT")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            return ResponseEntity.ok(new AuthResponse(token, role));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Email ou mot de passe incorrect");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Une erreur est survenue lors de l'authentification");
        }
    }

    @PostMapping("/signup/enseignant")
    @Operation(summary = "Inscription enseignant", description = "Crée un nouveau compte enseignant")
    public ResponseEntity<?> registerEnseignant(@RequestBody EnseignantSignupRequest enseignantRequest) {
        try {
            // Vérification dans les deux tables
            if (emailExistsInAnyRepository(enseignantRequest.getEmail())) {
                return ResponseEntity.badRequest().body("Email déjà utilisé");
            }

            Enseignant enseignant = new Enseignant();
            enseignant.setNom(enseignantRequest.getNom());
            enseignant.setPrenom(enseignantRequest.getPrenom());
            enseignant.setEmail(enseignantRequest.getEmail());
            enseignant.setMotDePasse(passwordEncoder.encode(enseignantRequest.getMotDePasse()));
            enseignant.setTelephone(enseignantRequest.getTelephone());
            enseignant.setGrade(enseignantRequest.getGrade());
            enseignant.setDepartement(enseignantRequest.getDepartement());

            enseignantRepository.save(enseignant);
            return ResponseEntity.ok("Enseignant enregistré avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de l'inscription");
        }
    }

    @PostMapping("/signup/admin")
    @Operation(summary = "Inscription administrateur", description = "Crée un nouveau compte administrateur")
    public ResponseEntity<?> registerAdmin(@RequestBody AdminSignupRequest adminRequest) {
        try {
            // Vérification dans les deux tables
            if (emailExistsInAnyRepository(adminRequest.getEmail())) {
                return ResponseEntity.badRequest().body("Email déjà utilisé");
            }

            Administrateur admin = new Administrateur();
            admin.setNom(adminRequest.getNom());
            admin.setPrenom(adminRequest.getPrenom());
            admin.setEmail(adminRequest.getEmail());
            admin.setMotDePasse(passwordEncoder.encode(adminRequest.getMotDePasse()));
            admin.setTelephone(adminRequest.getTelephone());
            admin.setFonction(adminRequest.getFonction());

            administrateurRepository.save(admin);
            return ResponseEntity.ok("Administrateur enregistré avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de l'inscription");
        }
    }

    private boolean emailExistsInAnyRepository(String email) {
        return administrateurRepository.existsByEmail(email) || enseignantRepository.existsByEmail(email);
    }
}