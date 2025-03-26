package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.SurveillanceDTO;
import tn.fst.spring.backend_pfs_s2.model.Surveillance;
import tn.fst.spring.backend_pfs_s2.service.SurveillanceService;



import jakarta.servlet.http.HttpServletResponse;  // Change this import

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/surveillances")
public class SurveillanceController {


    @Autowired
    private SurveillanceService surveillanceService;

    @GetMapping
    public List<SurveillanceDTO> getAllSurveillances() {
        return surveillanceService.getAllSurveillances().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SurveillanceDTO getSurveillanceById(@PathVariable Long id) {
        Surveillance surveillance = surveillanceService.getSurveillanceById(id);
        return convertToDTO(surveillance);
    }

    @PostMapping
    public SurveillanceDTO createSurveillance(@RequestBody SurveillanceDTO surveillanceDTO) {
        Surveillance surveillance = convertToEntity(surveillanceDTO);
        Surveillance createdSurveillance = surveillanceService.createSurveillance(surveillance);
        return convertToDTO(createdSurveillance);
    }

    @PutMapping("/{id}")
    public SurveillanceDTO updateSurveillance(@PathVariable Long id, @RequestBody SurveillanceDTO surveillanceDTO) {
        Surveillance surveillance = convertToEntity(surveillanceDTO);
        Surveillance updatedSurveillance = surveillanceService.updateSurveillance(id, surveillance);
        return convertToDTO(updatedSurveillance);
    }

    @DeleteMapping("/{id}")
    public void deleteSurveillance(@PathVariable Long id) {
        surveillanceService.deleteSurveillance(id);
    }

    private SurveillanceDTO convertToDTO(Surveillance surveillance) {
        SurveillanceDTO dto = new SurveillanceDTO();
        dto.setId(surveillance.getId());
        dto.setDateDebut(surveillance.getDateDebut());
        dto.setDateFin(surveillance.getDateFin());
        dto.setStatut(surveillance.getStatut());

        // Vérification null pour salle
        dto.setSalleId(surveillance.getSalle() != null ? surveillance.getSalle().getId() : null);

        // Vérification null pour matiere
        dto.setMatiereId(surveillance.getMatiere() != null ? surveillance.getMatiere().getId() : null);

        // Vérification null pour enseignantPrincipal
        dto.setEnseignantPrincipalId(surveillance.getEnseignantPrincipal() != null
                ? surveillance.getEnseignantPrincipal().getId() : null);

        // Vérification null pour enseignantSecondaire
        dto.setEnseignantSecondaireId(surveillance.getEnseignantSecondaire() != null
                ? surveillance.getEnseignantSecondaire().getId() : null);

        // Vérification null pour sessionExamen
        dto.setSessionExamenId(surveillance.getSessionExamen() != null
                ? surveillance.getSessionExamen().getId() : null);

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


    @GetMapping(value = "/export/csv", produces = "text/csv;charset=UTF-8")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"surveillances.csv\"");
        response.setCharacterEncoding("UTF-8");

        // Write UTF-8 BOM for Excel compatibility
        byte[] bom = new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF };
        response.getOutputStream().write(bom);

        Writer writer = new BufferedWriter(new OutputStreamWriter(
                response.getOutputStream(), StandardCharsets.UTF_8
        ));
        surveillanceService.generateCsv(writer);
    }

    @GetMapping(value = "/export/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"surveillances.xlsx\"");

        surveillanceService.generateExcel(response.getOutputStream());
    }/*
    @GetMapping(value = "/convocation/{enseignantId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public void generateConvocation(@PathVariable Long enseignantId, HttpServletResponse response)
            throws IOException {
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-Disposition",
                "attachment; filename=\"convocation_surveillance.pdf\"");

        surveillanceService.generateConvocationPdf(enseignantId, response.getOutputStream());
    }
*/


}
