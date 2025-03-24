package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.AuthRequest;
import tn.fst.spring.backend_pfs_s2.dto.AuthResponse;
import tn.fst.spring.backend_pfs_s2.dto.SignupRequest;
import tn.fst.spring.backend_pfs_s2.model.Administrateur;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import tn.fst.spring.backend_pfs_s2.repository.AdministrateurRepository;
import tn.fst.spring.backend_pfs_s2.repository.EnseignantRepository;
import tn.fst.spring.backend_pfs_s2.service.JwtService;
import tn.fst.spring.backend_pfs_s2.service.CustomUserDetailsService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtService JwtService;

    @Autowired
    private AdministrateurRepository administrateurRepository;

    @Autowired
    private EnseignantRepository enseignantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Incorrect email or password", e);
        }

        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(authRequest.getEmail());
        final String jwt = JwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        if (signupRequest.getRole().equals("ADMIN")) {
            Administrateur admin = new Administrateur(
                    signupRequest.getNom(),
                    signupRequest.getPrenom(),
                    signupRequest.getEmail(),
                    passwordEncoder.encode(signupRequest.getMotDePasse()),
                    signupRequest.getTelephone(),
                    signupRequest.getFonction()
            );
            administrateurRepository.save(admin);
        } else if (signupRequest.getRole().equals("ENSEIGNANT")) {
            Enseignant enseignant = new Enseignant(
                    signupRequest.getNom(),
                    signupRequest.getPrenom(),
                    signupRequest.getEmail(),
                    passwordEncoder.encode(signupRequest.getMotDePasse()),
                    signupRequest.getTelephone(),
                    signupRequest.getGrade(),
                    signupRequest.getDepartement(),
                    signupRequest.getEstDisponible()
            );
            enseignantRepository.save(enseignant);
        } else {
            return ResponseEntity.badRequest().body("Invalid role");
        }

        return ResponseEntity.ok("User registered successfully");
    }
}