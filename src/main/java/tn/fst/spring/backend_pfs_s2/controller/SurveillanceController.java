package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.EntityNotFoundException; // Ajoutez cet import
import org.springframework.http.HttpStatus;          // Ajoutez cet import
import org.springframework.http.ResponseEntity;     // Ajoutez cet import
import org.springframework.security.access.annotation.Secured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.AssignementRequestDTO; // Ajoutez cet import
import tn.fst.spring.backend_pfs_s2.dto.SurveillanceDTO;
import tn.fst.spring.backend_pfs_s2.model.Surveillance;
import tn.fst.spring.backend_pfs_s2.service.export.ConvocationService;
import tn.fst.spring.backend_pfs_s2.model.*; // Assurez-vous que les modèles nécessaires sont importés
import tn.fst.spring.backend_pfs_s2.repository.EnseignantRepository; // Ajoutez si nécessaire pour la conversion
import tn.fst.spring.backend_pfs_s2.repository.MatiereRepository;     // Ajoutez si nécessaire pour la conversion
import tn.fst.spring.backend_pfs_s2.repository.SalleRepository;       // Ajoutez si nécessaire pour la conversion
import tn.fst.spring.backend_pfs_s2.repository.SessionExamenRepository; // Ajoutez si nécessaire pour la conversion
import tn.fst.spring.backend_pfs_s2.service.SurveillanceService;



import jakarta.servlet.http.HttpServletResponse;  // Change this import

import java.io.IOException;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/api/surveillances")
@Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"}) // Ajustez les rôles si nécessaire
public class SurveillanceController {


    @Autowired
    private SurveillanceService surveillanceService;
    @Autowired
    private ConvocationService convocationService;

    // Injectez les repositories si nécessaire pour la conversion dans convertToEntity
    private final SalleRepository salleRepository;
    private final MatiereRepository matiereRepository;
    private final SessionExamenRepository sessionExamenRepository;
    private final EnseignantRepository enseignantRepository;


    // Modifiez le constructeur pour inclure les nouveaux repositories
    public SurveillanceController(SurveillanceService surveillanceService,
                                  SalleRepository salleRepository,
                                  MatiereRepository matiereRepository,
                                  SessionExamenRepository sessionExamenRepository,
                                  EnseignantRepository enseignantRepository) {
        this.surveillanceService = surveillanceService;
        this.salleRepository = salleRepository;
        this.matiereRepository = matiereRepository;
        this.sessionExamenRepository = sessionExamenRepository;
        this.enseignantRepository = enseignantRepository;
    }


    @GetMapping
    public List<SurveillanceDTO> getAllSurveillances() {
        return surveillanceService.getAllSurveillances().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurveillanceDTO> getSurveillanceById(@PathVariable Long id) {
        try {
            Surveillance surveillance = surveillanceService.getSurveillanceById(id);
            if (surveillance == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(convertToDTO(surveillance));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<SurveillanceDTO> createSurveillance(@RequestBody SurveillanceDTO surveillanceDTO) {
        try {
            Surveillance surveillanceToCreate = convertToEntity(surveillanceDTO);
            // Ne pas définir les enseignants ici, utiliser /assign
            surveillanceToCreate.setEnseignantPrincipal(null);
            surveillanceToCreate.setEnseignantSecondaire(null);

            Surveillance created = surveillanceService.createSurveillance(surveillanceToCreate);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(created));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(null); // Ou un message d'erreur plus spécifique
        } catch (Exception e) {
            // Log l'erreur e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<SurveillanceDTO> updateSurveillance(@PathVariable Long id, @RequestBody SurveillanceDTO surveillanceDTO) {
        try {
            Surveillance surveillanceDetails = convertToEntity(surveillanceDTO);
            // L'affectation des enseignants se fait via /assign
            // Les IDs enseignants dans le DTO pour cette route seront ignorés par le service update
            Surveillance updated = surveillanceService.updateSurveillance(id, surveillanceDetails);
            return ResponseEntity.ok(convertToDTO(updated));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Log l'erreur e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // NOUVEAU ENDPOINT POUR L'AFFECTATION
    @PutMapping("/{surveillanceId}/assign")
    @Secured("ROLE_ADMIN") // Seul l'admin peut affecter
    public ResponseEntity<?> assignEnseignantsToSurveillance(
            @PathVariable Long surveillanceId,
            @RequestBody AssignementRequestDTO assignementRequest) {
        try {
            Surveillance updatedSurveillance = surveillanceService.assignEnseignants(
                    surveillanceId,
                    assignementRequest.getEnseignantPrincipalId(),
                    assignementRequest.getEnseignantSecondaireId()
            );
            return ResponseEntity.ok(convertToDTO(updatedSurveillance));
        } catch (EntityNotFoundException e) {
            // Entité non trouvée (Surveillance, Enseignant)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            // Erreur métier (Non disponible, Conflit)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // Mauvaise requête (ex: même enseignant principal/secondaire)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Autres erreurs internes
            // Log l'erreur e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur interne est survenue lors de l'affectation.");
        }
    }


    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> deleteSurveillance(@PathVariable Long id) {
        try {
            surveillanceService.deleteSurveillance(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Log l'erreur e
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- Méthodes de Conversion ---

    private SurveillanceDTO convertToDTO(Surveillance surveillance) {
        // (Garder la méthode convertToDTO existante)
        SurveillanceDTO dto = new SurveillanceDTO();
        dto.setId(surveillance.getId());
        dto.setDateDebut(surveillance.getDateDebut());
        dto.setDateFin(surveillance.getDateFin());
        dto.setStatut(surveillance.getStatut());
        if (surveillance.getSalle() != null) {
            dto.setSalleId(surveillance.getSalle().getId());
            dto.setSalleName(surveillance.getSalle().getNumero());
        }
        if (surveillance.getMatiere() != null) {
            dto.setMatiereId(surveillance.getMatiere().getId());
            dto.setMatiereName(surveillance.getMatiere().getNom());
        }
        if (surveillance.getEnseignantPrincipal() != null) {
            dto.setEnseignantPrincipalId(surveillance.getEnseignantPrincipal().getId());
        }
        if (surveillance.getEnseignantSecondaire() != null) {
            dto.setEnseignantSecondaireId(surveillance.getEnseignantSecondaire().getId());
        }
        if (surveillance.getSessionExamen() != null) {
            dto.setSessionExamenId(surveillance.getSessionExamen().getId());
        }
        return dto;

    }

    private Surveillance convertToEntity(SurveillanceDTO dto) {
        // (Garder et adapter la méthode convertToEntity existante)
        Surveillance surveillance = new Surveillance();
        surveillance.setId(dto.getId()); // Important pour la mise à jour
        surveillance.setDateDebut(dto.getDateDebut());
        surveillance.setDateFin(dto.getDateFin());
        surveillance.setStatut(dto.getStatut());

        // Récupérer les entités liées à partir des IDs
        if (dto.getSalleId() != null) {
            surveillance.setSalle(salleRepository.findById(dto.getSalleId())
                    .orElseThrow(() -> new EntityNotFoundException("Salle non trouvée avec l'ID : " + dto.getSalleId())));
        }
        if (dto.getMatiereId() != null) {
            surveillance.setMatiere(matiereRepository.findById(dto.getMatiereId())
                    .orElseThrow(() -> new EntityNotFoundException("Matière non trouvée avec l'ID : " + dto.getMatiereId())));
        }
        if (dto.getSessionExamenId() != null) {
            surveillance.setSessionExamen(sessionExamenRepository.findById(dto.getSessionExamenId())
                    .orElseThrow(() -> new EntityNotFoundException("SessionExamen non trouvée avec l'ID : " + dto.getSessionExamenId())));
        }

        // IMPORTANT: Ne pas charger les enseignants ici pour la conversion générale.
        // L'affectation se fait via le service `assignEnseignants`.
        // Si on les chargeait ici, cela pourrait causer des problèmes lors d'un simple PUT pour mettre à jour la date/heure.
        // if (dto.getEnseignantPrincipalId() != null) {
        //    surveillance.setEnseignantPrincipal(enseignantRepository.findById(dto.getEnseignantPrincipalId()).orElse(null));
        // }
        // if (dto.getEnseignantSecondaireId() != null) {
        //    surveillance.setEnseignantSecondaire(enseignantRepository.findById(dto.getEnseignantSecondaireId()).orElse(null));
        // }

        return surveillance;
    }


    @GetMapping(value = "/export/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"surveillances.xlsx\"");

        surveillanceService.exportToExcel(response.getOutputStream());
    }
    @GetMapping(value = "/export/csv", produces = "text/csv;charset=UTF-8")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"surveillances.csv\"");
        response.setCharacterEncoding("UTF-8");

        // Write UTF-8 BOM for Excel compatibility
        byte[] bom = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF };
        response.getOutputStream().write(bom);

        // Directly use the OutputStream instead of creating a Writer
        surveillanceService.exportToCsv(response.getOutputStream());
    }

    @GetMapping("/enseignants/{id}/convocation")
    public ResponseEntity<byte[]> generateConvocation(@PathVariable Long id) {
        try {
            byte[] pdfContent = convocationService.generateConvocation(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=convocation.pdf")
                    .body(pdfContent);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // NEW descriptive method to get surveillances by session ID
    @GetMapping("/session/{sessionId}")
    @Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"}) // Added explicit security annotation
    public ResponseEntity<List<SurveillanceDTO>> getSurveillancesForSession(@PathVariable Long sessionId) {
        try {
            List<Surveillance> surveillances = surveillanceService.getSurveillancesBySessionId(sessionId);
            return ResponseEntity.ok(surveillances.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}