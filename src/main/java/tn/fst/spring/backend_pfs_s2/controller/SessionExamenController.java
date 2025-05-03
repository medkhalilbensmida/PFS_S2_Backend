package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import tn.fst.spring.backend_pfs_s2.dto.EnseignantDTO;
import tn.fst.spring.backend_pfs_s2.dto.SessionExamenDTO;
import tn.fst.spring.backend_pfs_s2.dto.SessionExamenDetailsDTO;
import tn.fst.spring.backend_pfs_s2.dto.SurveillanceDetailsDTO;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import tn.fst.spring.backend_pfs_s2.model.AnneeUniversitaire;
import tn.fst.spring.backend_pfs_s2.model.Semestre;
import tn.fst.spring.backend_pfs_s2.model.SessionExamen;
import tn.fst.spring.backend_pfs_s2.repository.SurveillanceRepository;
import tn.fst.spring.backend_pfs_s2.service.SessionExamenService;
import tn.fst.spring.backend_pfs_s2.service.SurveillanceService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sessions")
@Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"})
@RequiredArgsConstructor
public class SessionExamenController {

    private final SessionExamenService sessionService;
    private final SurveillanceService surveillanceService;



    


    @GetMapping
    public ResponseEntity<List<SessionExamenDTO>> getAllSessions() {
        List<SessionExamenDTO> sessions = sessionService.getAllSessions().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sessions);
    }



    @GetMapping("/{id}")
    public ResponseEntity<?> getSessionById(@PathVariable Long id) {
        SessionExamen session = sessionService.getSessionById(id);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(session));
    }

    @PostMapping
    public ResponseEntity<?> createSession(@RequestBody SessionExamenDTO sessionDTO) {
        try {
            SessionExamen session = convertToEntity(sessionDTO);
            SessionExamen created = sessionService.createSession(session);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(convertToDTO(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("message",
                            "Erreur lors de la création de la session: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSession(@PathVariable Long id, @RequestBody SessionExamenDTO sessionDTO) {
        try {
            SessionExamen session = convertToEntity(sessionDTO);
            SessionExamen updated = sessionService.updateSession(id, session);
            if (updated == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(convertToDTO(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("message",
                            "Erreur lors de la mise à jour de la session: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSession(@PathVariable Long id) {
        try {
            sessionService.deleteSession(id);
            return ResponseEntity.ok()
                    .body(Collections.singletonMap("message", "Session supprimée avec succès"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("message",
                            "Erreur lors de la suppression de la session: " + e.getMessage()));
        }
    }

    private SessionExamenDTO convertToDTO(SessionExamen session) {
        SessionExamenDTO dto = new SessionExamenDTO();
        dto.setId(session.getId());
        dto.setDateDebut(session.getDateDebut());
        dto.setDateFin(session.getDateFin());
        dto.setType(session.getType());
        dto.setEstActive(session.getEstActive());

        if (session.getAnnee() != null) {
            dto.setAnneeUniversitaireId(session.getAnnee().getId());
        }

        if (session.getNumSemestre() != null) {
            dto.setNumSemestre(session.getNumSemestre().toString());
        }

        return dto;
    }

    private SessionExamen convertToEntity(SessionExamenDTO dto) {
        SessionExamen session = new SessionExamen();
        session.setDateDebut(dto.getDateDebut());
        session.setDateFin(dto.getDateFin());
        session.setType(dto.getType());
        session.setEstActive(dto.getEstActive());

        if (dto.getAnneeUniversitaireId() != null) {
            AnneeUniversitaire annee = new AnneeUniversitaire();
            annee.setId(dto.getAnneeUniversitaireId());
            session.setAnnee(annee);
        }

        if (dto.getNumSemestre() != null) {
            session.setNumSemestre(Semestre.valueOf(dto.getNumSemestre()));
        }

        return session;
    }

    @GetMapping("/detailed/{id}")
    public SessionExamenDetailsDTO getSessionDetailsByID(@PathVariable Long id){
        SessionExamen session = sessionService.getSessionWithDetails(id);
        return convertToDetailedDTO(session);
    }
    

    @GetMapping("/{sessionId}/enseignants")
    public List<EnseignantDTO> getEnseignantsForSession(@PathVariable Long sessionId) {
        return surveillanceService.getEnseignantsForSession(sessionId);
    }





    private SessionExamenDetailsDTO convertToDetailedDTO(SessionExamen session) {
        if (session == null) return null;
        SessionExamenDetailsDTO dto = new SessionExamenDetailsDTO();
        dto.setId(session.getId());
        dto.setDateDebut(session.getDateDebut());
        dto.setDateFin(session.getDateFin());
        dto.setType(session.getType().name());
        dto.setEstActive(session.getEstActive());
        dto.setNumSemestre(session.getNumSemestre().toString());

        if (session.getAnnee() != null) {
            dto.setAnneeUniversitaireId(session.getAnnee().getId());
        }

        if (session.getSurveillances() != null) {
            dto.setSurveillances(
                session.getSurveillances().stream().map(surv -> {
                    SurveillanceDetailsDTO sDto = surveillanceService.getDetailedSurveillanceFromSurveillance(surv);
                    return sDto;
                }).toList()
            );
        }

        return dto;
    }
        

}