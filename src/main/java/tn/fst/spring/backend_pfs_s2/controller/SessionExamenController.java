package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.SessionExamenDTO;
import tn.fst.spring.backend_pfs_s2.dto.SessionExamenDetailsDTO;
import tn.fst.spring.backend_pfs_s2.dto.SurveillanceDetailsDTO;
import tn.fst.spring.backend_pfs_s2.model.Semestre;
import tn.fst.spring.backend_pfs_s2.model.SessionExamen;
import tn.fst.spring.backend_pfs_s2.service.SessionExamenService;
import tn.fst.spring.backend_pfs_s2.service.SurveillanceService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sessions")
@Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"})
public class SessionExamenController {

    private final SessionExamenService sessionService;
    private final SurveillanceService surveillanceService;

    public SessionExamenController(SessionExamenService sessionService,SurveillanceService surveillanceService) {
        this.sessionService = sessionService;
        this.surveillanceService = surveillanceService;
    }



    @GetMapping
    public List<SessionExamenDTO> getAllSessions() {
        return sessionService.getAllSessions().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public SessionExamenDTO getSessionById(@PathVariable Long id) {
        return convertToDTO(sessionService.getSessionById(id));
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public SessionExamenDTO createSession(@RequestBody SessionExamenDTO sessionDTO) {
        SessionExamen created = sessionService.createSession(convertToEntity(sessionDTO));
        return convertToDTO(created);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public SessionExamenDTO updateSession(@PathVariable Long id, @RequestBody SessionExamenDTO sessionDTO) {
        SessionExamen updated = sessionService.updateSession(id, convertToEntity(sessionDTO));
        return convertToDTO(updated);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public void deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
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
        dto.setNumSemestre(session.getNumSemestre().toString());
        return dto;
    }

    private SessionExamen convertToEntity(SessionExamenDTO dto) {
        SessionExamen session = new SessionExamen();
        session.setId(dto.getId());
        session.setDateDebut(dto.getDateDebut());
        session.setDateFin(dto.getDateFin());
        session.setType(dto.getType());
        session.setEstActive(dto.getEstActive());
        session.setNumSemestre(Semestre.valueOf(dto.getNumSemestre()));
        return session;
    }

    @GetMapping("/detailed/{id}")
    public SessionExamenDetailsDTO getSessionDetailsByID(@PathVariable Long id){
        SessionExamen session = sessionService.getSessionWithDetails(id);
        return convertToDetailedDTO(session);
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