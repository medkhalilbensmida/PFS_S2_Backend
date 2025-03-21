package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.SessionExamenDTO;
import tn.fst.spring.backend_pfs_s2.model.Semestre;
import tn.fst.spring.backend_pfs_s2.model.SessionExamen;
import tn.fst.spring.backend_pfs_s2.service.SessionExamenService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sessions")
public class SessionExamenController {

    @Autowired
    private SessionExamenService sessionExamenService;

    @GetMapping
    public List<SessionExamenDTO> getAllSessions() {
        return sessionExamenService.getAllSessions().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SessionExamenDTO getSessionById(@PathVariable Long id) {
        SessionExamen sessionExamen = sessionExamenService.getSessionById(id);
        return convertToDTO(sessionExamen);
    }

    @PostMapping
    public SessionExamenDTO createSession(@RequestBody SessionExamenDTO sessionExamenDTO) {
        SessionExamen sessionExamen = convertToEntity(sessionExamenDTO);
        SessionExamen createdSession = sessionExamenService.createSession(sessionExamen);
        return convertToDTO(createdSession);
    }

    @PutMapping("/{id}")
    public SessionExamenDTO updateSession(@PathVariable Long id, @RequestBody SessionExamenDTO sessionExamenDTO) {
        SessionExamen sessionExamen = convertToEntity(sessionExamenDTO);
        SessionExamen updatedSession = sessionExamenService.updateSession(id, sessionExamen);
        return convertToDTO(updatedSession);
    }

    @DeleteMapping("/{id}")
    public void deleteSession(@PathVariable Long id) {
        sessionExamenService.deleteSession(id);
    }

    private SessionExamenDTO convertToDTO(SessionExamen sessionExamen) {
        SessionExamenDTO dto = new SessionExamenDTO();
        dto.setId(sessionExamen.getId());
        dto.setDateDebut(sessionExamen.getDateDebut());
        dto.setDateFin(sessionExamen.getDateFin());
        dto.setType(sessionExamen.getType());
        dto.setEstActive(sessionExamen.getEstActive());
        dto.setAnneeUniversitaireId(sessionExamen.getAnnee().getId());
        dto.setNumSemestre(String.valueOf(sessionExamen.getNumSemestre()));
        return dto;
    }

    private SessionExamen convertToEntity(SessionExamenDTO dto) {
        SessionExamen sessionExamen = new SessionExamen();
        sessionExamen.setId(dto.getId());
        sessionExamen.setDateDebut(dto.getDateDebut());
        sessionExamen.setDateFin(dto.getDateFin());
        sessionExamen.setType(dto.getType());
        sessionExamen.setEstActive(dto.getEstActive());
        // Vous devez récupérer l'AnneeUniversitaire par son ID ici
        sessionExamen.setNumSemestre(Semestre.valueOf(dto.getNumSemestre()));
        return sessionExamen;
    }
}
