package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.SurveillanceDTO;
import tn.fst.spring.backend_pfs_s2.model.Surveillance;
import tn.fst.spring.backend_pfs_s2.service.export.ConvocationService;
import tn.fst.spring.backend_pfs_s2.service.SurveillanceService;



import jakarta.servlet.http.HttpServletResponse;  // Change this import

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/api/surveillances")
@Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"})
public class SurveillanceController {


    @Autowired
    private SurveillanceService surveillanceService;
    @Autowired
    private ConvocationService convocationService;


    public SurveillanceController(SurveillanceService surveillanceService) {
        this.surveillanceService = surveillanceService;
    }

    @GetMapping
    public List<SurveillanceDTO> getAllSurveillances() {
        return surveillanceService.getAllSurveillances().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SurveillanceDTO getSurveillanceById(@PathVariable Long id) {
        return convertToDTO(surveillanceService.getSurveillanceById(id));
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public SurveillanceDTO createSurveillance(@RequestBody SurveillanceDTO surveillanceDTO) {
        Surveillance created = surveillanceService.createSurveillance(convertToEntity(surveillanceDTO));
        return convertToDTO(created);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public SurveillanceDTO updateSurveillance(@PathVariable Long id, @RequestBody SurveillanceDTO surveillanceDTO) {
        Surveillance updated = surveillanceService.updateSurveillance(id, convertToEntity(surveillanceDTO));
        return convertToDTO(updated);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public void deleteSurveillance(@PathVariable Long id) {
        surveillanceService.deleteSurveillance(id);
    }

    private SurveillanceDTO convertToDTO(Surveillance surveillance) {
        SurveillanceDTO dto = new SurveillanceDTO();
        dto.setId(surveillance.getId());
        dto.setDateDebut(surveillance.getDateDebut());
        dto.setDateFin(surveillance.getDateFin());
        dto.setStatut(surveillance.getStatut());
        if (surveillance.getSalle() != null) {
            dto.setSalleId(surveillance.getSalle().getId());
        }
        if (surveillance.getMatiere() != null) {
            dto.setMatiereId(surveillance.getMatiere().getId());
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
        Surveillance surveillance = new Surveillance();
        surveillance.setId(dto.getId());
        surveillance.setDateDebut(dto.getDateDebut());
        surveillance.setDateFin(dto.getDateFin());
        surveillance.setStatut(dto.getStatut());

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
}
