package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

import java.util.Map;

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

            // Vérifier le type d'utilisateur et retourner la réponse appropriée
            if (role.equals("ROLE_ADMIN")) {
                Administrateur admin = administrateurRepository.findByEmail(authRequest.getEmail())
                        .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));
                return ResponseEntity.ok(new AuthResponseAdministrateur(
                        token,
                        role,
                        admin.getId(),
                        admin.getNom(),
                        admin.getPrenom(),
                        admin.getEmail(),
                        admin.getTelephone(),
                        admin.getFonction()
                ));
            } else {
                Enseignant enseignant = enseignantRepository.findByEmail(authRequest.getEmail())
                        .orElseThrow(() -> new UsernameNotFoundException("Enseignant not found"));
                return ResponseEntity.ok(new AuthResponseEnseignant(
                        token,
                        role,
                        enseignant.getId(),
                        enseignant.getNom(),
                        enseignant.getPrenom(),
                        enseignant.getEmail(),
                        enseignant.getTelephone(),
                        enseignant.getGrade(),
                        enseignant.getDepartement()
                ));
            }
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
                return ResponseEntity
                        .badRequest()
                        .body(new ErrorResponse("EMAIL_EXISTS", "Cet email est déjà utilisé"));
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
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("message", "Enseignant enregistré avec succès"));
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
                return ResponseEntity
                        .badRequest()
                        .body(new ErrorResponse("EMAIL_EXISTS", "Cet email est déjà utilisé"));
            }

            Administrateur admin = new Administrateur();
            admin.setNom(adminRequest.getNom());
            admin.setPrenom(adminRequest.getPrenom());
            admin.setEmail(adminRequest.getEmail());
            admin.setMotDePasse(passwordEncoder.encode(adminRequest.getMotDePasse()));
            admin.setTelephone(adminRequest.getTelephone());
            admin.setFonction(adminRequest.getFonction());

            administrateurRepository.save(admin);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("message", "Administrateur enregistré avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de l'inscription");
        }
    }

    private boolean emailExistsInAnyRepository(String email) {
        return administrateurRepository.existsByEmail(email) || enseignantRepository.existsByEmail(email);
    }
}
