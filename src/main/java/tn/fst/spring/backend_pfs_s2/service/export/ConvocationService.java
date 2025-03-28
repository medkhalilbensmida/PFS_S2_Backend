package tn.fst.spring.backend_pfs_s2.service.export;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.model.*;
import tn.fst.spring.backend_pfs_s2.repository.*;
import tn.fst.spring.backend_pfs_s2.service.SurveillanceService;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import com.itextpdf.layout.borders.Border;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConvocationService {

    @Autowired
    private SurveillanceService surveillanceService;

    @Autowired
    private EnseignantRepository enseignantRepository;
    @Autowired
    private SessionExamenRepository sessionExamenRepository;

    private void addHeader(Document document, SurveillanceFilterDTO filterDTO) {
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
        headerTable.setWidth(UnitValue.createPercentValue(100));
        headerTable.setMinHeight(60); // Add minimum height to ensure vertical space

        // Left cell
        Cell leftCell = new Cell();
        leftCell.setBorder(Border.NO_BORDER);
        leftCell.setVerticalAlignment(VerticalAlignment.MIDDLE); // Add vertical alignment

        Paragraph leftParagraph = new Paragraph();
        leftParagraph.add(new Text("Université Tunis El Manar").setBold())
                .add("\nFaculté des Sciences de Tunis")
                .add("\nDépartement des Sciences Informatiques");
        leftParagraph.setTextAlignment(TextAlignment.LEFT)
                .setFontSize(12);

        leftCell.add(leftParagraph);

        // Right cell
        Cell rightCell = new Cell();
        rightCell.setBorder(Border.NO_BORDER);
        rightCell.setVerticalAlignment(VerticalAlignment.MIDDLE); // Add vertical alignment

        String yearText = "Année Universitaire: " +
                (filterDTO.getAnneeUniversitaire() != null ?
                        filterDTO.getAnneeUniversitaire() : "Toutes");

        Paragraph rightParagraph = new Paragraph(yearText)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(12);

        rightCell.add(rightParagraph);

        // Add cells to table
        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);
        document.add(new Paragraph("\n"));
    }
    private void addTitle(Document document, SurveillanceFilterDTO filterDTO) {
        StringBuilder titleBuilder = new StringBuilder();
        titleBuilder.append("CONVOCATION POUR LA SURVEILLANCE DES EXAMENS\n");


        // Add session type from filter if available, otherwise from current session
        if (filterDTO.getTypeSession() != null) {
            titleBuilder.append("Session ");
            titleBuilder.append(filterDTO.getTypeSession());
        } else {
            titleBuilder.append("TOUTES LES SESSIONS");
        }

        // Add semester from filter if available
        if (filterDTO.getSemestre() != null) {
            titleBuilder.append(" ").append(filterDTO.getSemestre());
        }

        document.add(new Paragraph(titleBuilder.toString())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14)
                .setBold());
    }



    public byte[] generateConvocation(Long enseignantId, SurveillanceFilterDTO filterDTO) throws Exception {
        Enseignant enseignant = enseignantRepository.findById(enseignantId)
                .orElseThrow(() -> new EntityNotFoundException("Enseignant non trouvé"));

        // Get filtered surveillances for this teacher
        List<Surveillance> filteredSurveillances = surveillanceService
                .filterSurveillances(filterDTO)
                .stream()
                .filter(s -> (s.getEnseignantPrincipal() != null &&
                        s.getEnseignantPrincipal().getId().equals(enseignantId)) ||
                        (s.getEnseignantSecondaire() != null &&
                                s.getEnseignantSecondaire().getId().equals(enseignantId)))
                .collect(Collectors.toList());

        // Get current active session for header
        SessionExamen currentSession = sessionExamenRepository.findAll().stream()
                .filter(SessionExamen::getEstActive)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Aucune session active trouvée"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            addHeader(document, filterDTO); // Pass filterDTO here
            addTitle(document, filterDTO);
            addTeacherInfo(document, enseignant);
            addSurveillancesTable(document, enseignant, filteredSurveillances);
            addRecapTable(document, enseignant, filteredSurveillances);
            addSummary(document, enseignant, filteredSurveillances);
        } finally {
            document.close();
        }

        return baos.toByteArray();
    }

    private void addSurveillancesTable(Document document, Enseignant enseignant, List<Surveillance> surveillances) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{20, 15, 15, 30, 20}));
        table.setWidth(UnitValue.createPercentValue(100));

        // Add headers
        addCell(table, "Date", true);
        addCell(table, "Début", true);
        addCell(table, "Fin", true);
        addCell(table, "Matière", true);
        addCell(table, "Salle", true);

        // Add data rows if any exist
        if (!surveillances.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            surveillances.stream()
                    .filter(s -> s.getStatut() == StatutSurveillance.PLANIFIEE)
                    .forEach(s -> {
                        addCell(table, dateFormat.format(s.getDateDebut()), false);
                        addCell(table, timeFormat.format(s.getDateDebut()), false);
                        addCell(table, timeFormat.format(s.getDateFin()), false);
                        addCell(table, s.getMatiere().getNom(), false);
                        addCell(table, s.getSalle().getNumero(), false);
                    });
        } else {
            // Add an empty row with a message
            Cell emptyCell = new Cell(1, 5)
                    .add(new Paragraph("Aucune surveillance trouvée"))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic();
            table.addCell(emptyCell);
        }

        document.add(table);
    }

    private void addRecapTable(Document document, Enseignant enseignant, List<Surveillance> surveillances) {
        Table recapTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}));
        recapTable.setWidth(UnitValue.createPercentValue(100));

        // Headers
        recapTable.addCell(new Cell().add(new Paragraph("Type").setBold()));
        recapTable.addCell(new Cell().add(new Paragraph("Nombre de surveillances").setBold()));

        // Count DS surveillances
        long dsCount = surveillances.stream()
                .filter(s -> s.getStatut() == StatutSurveillance.PLANIFIEE &&
                        s.getSessionExamen().getType() == TypeSession.DS)
                .count();

        // Only show DS count if there are DS surveillances
        if (dsCount > 0) {
            recapTable.addCell(new Cell().add(new Paragraph("Surveillances DS").setBold()));
            recapTable.addCell(new Cell().add(new Paragraph(String.valueOf(dsCount))));
        }

        // Count exam surveillances by niveau
        Map<String, Long> examSurveillancesByNiveau = surveillances.stream()
                .filter(s -> s.getStatut() == StatutSurveillance.PLANIFIEE &&
                        (s.getSessionExamen().getType() == TypeSession.PRINCIPALE ||
                                s.getSessionExamen().getType() == TypeSession.RATTRAPAGE))
                .collect(Collectors.groupingBy(
                        s -> s.getMatiere().getNiveau(),
                        Collectors.counting()
                ));

        // Add exam counts only for niveaux that have surveillances
        long totalExams = 0;
        for (Map.Entry<String, Long> entry : examSurveillancesByNiveau.entrySet()) {
            if (entry.getValue() > 0) {  // Only add if there are surveillances for this niveau
                recapTable.addCell(new Cell().add(new Paragraph("Examen " + entry.getKey())));
                recapTable.addCell(new Cell().add(new Paragraph(entry.getValue().toString())));
                totalExams += entry.getValue();
            }
        }

        // Only add total if there are any surveillances
        if (dsCount > 0 || totalExams > 0) {
            recapTable.addCell(new Cell().add(new Paragraph("TOTAL GÉNÉRAL").setBold()));
            recapTable.addCell(new Cell().add(new Paragraph(String.valueOf(dsCount + totalExams)).setBold()));

            document.add(new Paragraph("\nRécapitulatif des surveillances par type:\n").setBold());
            document.add(recapTable);
        } else {
            document.add(new Paragraph("\nAucune surveillance à récapituler").setItalic());
        }
    }



    private void addTeacherInfo(Document document, Enseignant enseignant) {
        document.add(new Paragraph(String.format("\nA l'attention de %s %s %s\n\n",
                enseignant.getGrade() != null ? enseignant.getGrade() : "Mr/Mme",
                enseignant.getPrenom(),
                enseignant.getNom()))
                .setFontSize(12));

        document.add(new Paragraph("Vous êtes prié(e) d'assurer la surveillance des épreuves selon le calendrier ci-après. Nous vous demandons de vous présenter dans la salle d'examen 10 minutes avant le début de l'épreuve.\nVeuillez également interdire strictement l'utilisation des téléphones par les étudiants pendant l'examen.\n" +
                "\n\n"));
    }


    private void addSummary(Document document, Enseignant enseignant, List<Surveillance> surveillances) {
        int totalSurveillances = surveillances.size();

        document.add(new Paragraph("\nRécapitulatif:\n")
                .setBold()
                .setFontSize(12));
        document.add(new Paragraph(String.format("Nombre total de surveillances: %d", totalSurveillances)));

        document.add(new Paragraph("\n\nSignature du Chef de Département")
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(50));
    }

    private void addCell(Table table, String content, boolean isHeader) {
        Cell cell = new Cell().add(new Paragraph(content));
        if (isHeader) {
            cell.setBold();
        }
        cell.setTextAlignment(TextAlignment.CENTER);
        table.addCell(cell);
    }

    public byte[] generateAllConvocations(SurveillanceFilterDTO filterDTO) throws Exception {
        // Get all teachers
        List<Enseignant> allEnseignants = enseignantRepository.findAll();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            boolean isFirstPage = true;

            for (Enseignant enseignant : allEnseignants) {
                // Get filtered surveillances for this teacher
                List<Surveillance> filteredSurveillances = surveillanceService
                        .filterSurveillances(filterDTO)
                        .stream()
                        .filter(s -> (s.getEnseignantPrincipal() != null &&
                                s.getEnseignantPrincipal().getId().equals(enseignant.getId())) ||
                                (s.getEnseignantSecondaire() != null &&
                                        s.getEnseignantSecondaire().getId().equals(enseignant.getId())))
                        .collect(Collectors.toList());

                // Only create a convocation if teacher has surveillances
                if (!filteredSurveillances.isEmpty()) {
                    if (!isFirstPage) {
                        // Add a new page for each teacher except the first one
                        document.add(new AreaBreak());
                    }

                    addHeader(document, filterDTO);
                    addTitle(document, filterDTO);
                    addTeacherInfo(document, enseignant);
                    addSurveillancesTable(document, enseignant, filteredSurveillances);
                    addRecapTable(document, enseignant, filteredSurveillances);
                    addSummary(document, enseignant, filteredSurveillances);

                    isFirstPage = false;
                }
            }

            // Add a message if no convocations were generated
            if (isFirstPage) {
                document.add(new Paragraph("Aucune convocation à générer pour les critères sélectionnés.")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(14)
                        .setBold());
            }
        } finally {
            document.close();
        }

        return baos.toByteArray();
    }
}