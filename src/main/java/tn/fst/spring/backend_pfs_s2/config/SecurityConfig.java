package tn.fst.spring.backend_pfs_s2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tn.fst.spring.backend_pfs_s2.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Désactiver CSRF
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/auth/**", // Autoriser l'accès à l'authentification
                                "/swagger-ui/**", // Autoriser l'accès à Swagger UI
                                "/v3/api-docs/**", // Autoriser l'accès à la documentation OpenAPI
                                "/swagger-resources/**", // Autoriser l'accès aux ressources Swagger
                                "/webjars/**" // Autoriser l'accès aux ressources WebJars
                        ).permitAll() // Ces endpoints sont accessibles sans authentification
                        .anyRequest().authenticated() // Tous les autres endpoints nécessitent une authentification
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Pas de session
                );

        // Ajouter le filtre JWT avant le filtre d'authentification par défaut
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}