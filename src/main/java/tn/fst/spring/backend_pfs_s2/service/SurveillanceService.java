package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import tn.fst.spring.backend_pfs_s2.model.*;
import tn.fst.spring.backend_pfs_s2.repository.*;

import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;

import java.io.IOException;
import java.io.OutputStream;

import tn.fst.spring.backend_pfs_s2.service.export.CsvExportService;
import tn.fst.spring.backend_pfs_s2.service.export.ExcelExportService;


@Service
public class SurveillanceService {

    @Autowired
    private SurveillanceRepository surveillanceRepository;

    @Autowired
    private EnseignantRepository enseignantRepository;

    @Autowired
    private DisponibiliteEnseignantRepository disponibiliteRepository;
    @Autowired
    private CsvExportService csvExportService;

    @Autowired
    private ExcelExportService excelExportService;



    public List<Surveillance> getAllSurveillances() {
        return surveillanceRepository.findAll();
    }

    public Surveillance getSurveillanceById(Long id) {
        return surveillanceRepository.findById(id).orElse(null);
    }

    public Surveillance createSurveillance(Surveillance surveillance) {
        Surveillance savedSurveillance = surveillanceRepository.save(surveillance);
        initDisponibilitesForSurveillance(savedSurveillance);
        return savedSurveillance;
    }

    private void initDisponibilitesForSurveillance(Surveillance surveillance) {
        List<Enseignant> enseignants = enseignantRepository.findAll();
        for (Enseignant enseignant : enseignants) {
            DisponibiliteEnseignant disponibilite = new DisponibiliteEnseignant();
            disponibilite.setEnseignant(enseignant);
            disponibilite.setSurveillance(surveillance);
            disponibilite.setEstDisponible(false); // Par défaut non disponible
            disponibiliteRepository.save(disponibilite);
        }
    }

    public Surveillance updateSurveillance(Long id, Surveillance surveillance) {
        if (surveillanceRepository.existsById(id)) {
            surveillance.setId(id);
            return surveillanceRepository.save(surveillance);
        }
        return null;
    }

    public void deleteSurveillance(Long id) {
        // Supprimer d'abord les disponibilités associées
        List<DisponibiliteEnseignant> disponibilites =
                disponibiliteRepository.findBySurveillanceId(id);
        disponibiliteRepository.deleteAll(disponibilites);

        // Puis supprimer la surveillance
        surveillanceRepository.deleteById(id);
    }

    public List<Surveillance> getAvailableSurveillances() {
        return surveillanceRepository.findAll().stream()
                .filter(surveillance ->
                        surveillance.getStatut() == StatutSurveillance.PLANIFIEE &&
                                isEnseignantAvailable(surveillance.getEnseignantPrincipal(), surveillance) &&
                                isEnseignantAvailable(surveillance.getEnseignantSecondaire(), surveillance)
                )
                .collect(Collectors.toList());
    }

    public void exportToCsv(OutputStream outputStream) throws IOException {
        List<Surveillance> surveillances = getAvailableSurveillances();
        csvExportService.export(surveillances, outputStream);
    }

    public void exportToExcel(OutputStream outputStream) throws IOException {
        List<Surveillance> surveillances = getAvailableSurveillances();
        excelExportService.export(surveillances, outputStream);
    }

    private boolean isEnseignantAvailable(Enseignant enseignant, Surveillance surveillance) {
        if (enseignant == null) return false;

        Optional<DisponibiliteEnseignant> disponibilite = disponibiliteRepository
                .findByEnseignantAndSurveillance(enseignant, surveillance);

        return disponibilite
                .map(DisponibiliteEnseignant::getEstDisponible)
                .orElse(false);
    }


    public List<Surveillance> getSurveillancesByEnseignant(Long enseignantId) {
        return surveillanceRepository.findAll().stream()
                .filter(s -> (s.getEnseignantPrincipal() != null && s.getEnseignantPrincipal().getId().equals(enseignantId)) ||
                        (s.getEnseignantSecondaire() != null && s.getEnseignantSecondaire().getId().equals(enseignantId)))
                .collect(Collectors.toList());
    }

   /* public List<DisponibiliteEnseignant> getDisponibilitesForSurveillance(Long surveillanceId) {
        return disponibiliteRepository.findBySurveillanceId(surveillanceId);
    }

    public List<DisponibiliteEnseignant> getDisponibilitesForEnseignant(Long enseignantId) {
        return disponibiliteRepository.findByEnseignantId(enseignantId);
    }

    public DisponibiliteEnseignant updateDisponibilite(Long id, Boolean estDisponible) {
        DisponibiliteEnseignant disponibilite = disponibiliteRepository.findById(id).orElse(null);
        if (disponibilite != null) {
            disponibilite.setEstDisponible(estDisponible);
            return disponibiliteRepository.save(disponibilite);
        }
        return null;
    }*/
    /*private String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(date);
    }

    private String formatEnseignant(Enseignant enseignant) {
        if (enseignant == null) return "";
        return String.format("%s %s", enseignant.getNom(), enseignant.getPrenom());
    }*/

/*
public void generateCsv(Writer writer) throws IOException {
    CSVFormat csvFormat = CSVFormat.Builder.create()
            .setDelimiter(',')
            .setHeader("ID", "Date Début", "Date Fin", "Session", "Matière",
                    "Enseignant Principal", "Enseignant Secondaire", "Salle")
            .build();

    try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
        List<Surveillance> allSurveillances = surveillanceRepository.findAll();

        // Filter surveillances where both teachers are available
        List<Surveillance> availableSurveillances = allSurveillances.stream()
            .filter(surveillance ->
                    // Check if status is PLANIFIEE and both teachers are available
                    surveillance.getStatut() == StatutSurveillance.PLANIFIEE &&
                            isEnseignantAvailable(surveillance.getEnseignantPrincipal(), surveillance) &&
                            isEnseignantAvailable(surveillance.getEnseignantSecondaire(), surveillance)
            )
            .toList();

        for (Surveillance surveillance : availableSurveillances) {
            csvPrinter.printRecord(Arrays.asList(
                    surveillance.getId(),
                    formatDate(surveillance.getDateDebut()),
                    formatDate(surveillance.getDateFin()),
                    surveillance.getSessionExamen() != null ? surveillance.getSessionExamen().getType() : "",
                    surveillance.getMatiere() != null ? surveillance.getMatiere().getNom() : "",
                    formatEnseignant(surveillance.getEnseignantPrincipal()),
                    formatEnseignant(surveillance.getEnseignantSecondaire()),
                    surveillance.getSalle() != null ? surveillance.getSalle().getNumero() : ""
            ));
        }
        csvPrinter.flush();
    }
}
*/



/*public void generateExcel(OutputStream outputStream) throws IOException {
    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Surveillances");

        // Create header row with style
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);


        String[] columns = {"ID", "Date Début", "Date Fin", "Session", "Matière",
                "Enseignant Principal", "Enseignant Secondaire", "Salle"};

        // Create header cells
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

        // Filter surveillances where both teachers are available
        List<Surveillance> availableSurveillances = surveillanceRepository.findAll().stream()
            .filter(surveillance ->
                    // Check if status is PLANIFIEE and both teachers are available
                    surveillance.getStatut() == StatutSurveillance.PLANIFIEE &&
                            isEnseignantAvailable(surveillance.getEnseignantPrincipal(), surveillance) &&
                            isEnseignantAvailable(surveillance.getEnseignantSecondaire(), surveillance)
            )
            .toList();

        int rowNum = 1;
        for (Surveillance surveillance : availableSurveillances) {
                Row row = sheet.createRow(rowNum++);

                // Create cells for each column
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(surveillance.getId());

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(formatDate(surveillance.getDateDebut()));

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(formatDate(surveillance.getDateFin()));

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(surveillance.getSessionExamen() != null ?
                    surveillance.getSessionExamen().getType().toString() : "");

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(surveillance.getMatiere() != null ?
                    surveillance.getMatiere().getNom() : "");

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(formatEnseignant(surveillance.getEnseignantPrincipal()));

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(formatEnseignant(surveillance.getEnseignantSecondaire()));

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(surveillance.getSalle() != null ?
                    surveillance.getSalle().getNumero() : "");
            }

            // Resize all columns to fit the content
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        }
    }*/


}