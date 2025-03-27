package tn.fst.spring.backend_pfs_s2.service.export;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
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

    private void addHeader(Document document, SessionExamen session) {
        // Left side header
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
        headerTable.setWidth(UnitValue.createPercentValue(100));

        Cell leftCell = new Cell();
        leftCell.add(new Paragraph("Université Tunis El Manar")
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(12)
                .setBold());
        leftCell.add(new Paragraph("Faculté des Sciences de Tunis")
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(12));
        leftCell.add(new Paragraph("Département des Sciences Informatiques")
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(12));
        leftCell.setBorder(Border.NO_BORDER);

        // Right side header
        Cell rightCell = new Cell();
        rightCell.add(new Paragraph(String.format("Année Universitaire %s",
                session.getAnnee().toString()))
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(12));
        rightCell.setBorder(Border.NO_BORDER);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);
        document.add(headerTable);
        document.add(new Paragraph("\n"));
    }

    private void addTitle(Document document, SessionExamen session) {
        document.add(new Paragraph(String.format("CONVOCATION POUR LA SURVEILLANCE DES EXAMENS\nSession %s %s",
                session.getType().toString(),
                session.getNumSemestre().toString()))
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14)
                .setBold());
    }



    public byte[] generateConvocation(Long enseignantId) throws Exception {
        Enseignant enseignant = enseignantRepository.findById(enseignantId)
                .orElseThrow(() -> new RuntimeException("Enseignant non trouvé"));

        // Get current session
        SessionExamen currentSession = sessionExamenRepository.findAll().stream()
                .filter(SessionExamen::getEstActive)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Aucune session active trouvée"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        addHeader(document, currentSession);
        addTitle(document, currentSession);
        addTeacherInfo(document, enseignant);
        addSurveillancesTable(document, enseignant);
        addRecapTable(document, enseignant);

        document.add(new Paragraph("\n\nSignature du Chef de Département")
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(50));

        document.close();
        return baos.toByteArray();
    }


   private void addRecapTable(Document document, Enseignant enseignant) {
       Table recapTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}));
       recapTable.setWidth(UnitValue.createPercentValue(100));

       // Headers
       recapTable.addCell(new Cell().add(new Paragraph("Type").setBold()));
       recapTable.addCell(new Cell().add(new Paragraph("Nombre de surveillances").setBold()));

       // Get all planned surveillances for this teacher
       List<Surveillance> allSurveillances = surveillanceService.getSurveillancesByEnseignant(enseignant.getId())
               .stream()
               .filter(s -> s.getStatut() == StatutSurveillance.PLANIFIEE && s.getSessionExamen() != null)
               .collect(Collectors.toList());

       // Count DS surveillances
       long dsCount = allSurveillances.stream()
               .filter(s -> s.getSessionExamen().getType() == TypeSession.DS)
               .count();

       // Add DS count at the beginning
       recapTable.addCell(new Cell().add(new Paragraph("Surveillances DS").setBold()));
       recapTable.addCell(new Cell().add(new Paragraph(String.valueOf(dsCount))));

       // Filter and count Exam surveillances by niveau
       Map<String, Long> examSurveillancesByNiveau = allSurveillances.stream()
               .filter(s -> (s.getSessionExamen().getType() == TypeSession.PRINCIPALE || s.getSessionExamen().getType() == TypeSession.RATTRAPAGE  ) )
               .collect(Collectors.groupingBy(
                       s -> s.getMatiere().getNiveau(),
                       Collectors.counting()
               ));

       // Add exam surveillances counts by section
       long totalExams = 0;
       for (Map.Entry<String, Long> entry : examSurveillancesByNiveau.entrySet()) {
           recapTable.addCell(new Cell().add(new Paragraph("Examen " + entry.getKey())));
           recapTable.addCell(new Cell().add(new Paragraph(entry.getValue().toString())));
           totalExams += entry.getValue();
       }

       // Add total exam surveillances
       if (!examSurveillancesByNiveau.isEmpty()) {
           recapTable.addCell(new Cell().add(new Paragraph("Total Examens").setBold()));
           recapTable.addCell(new Cell().add(new Paragraph(String.valueOf(totalExams)).setBold()));
       }

       // Add grand total (DS + Exams)
       recapTable.addCell(new Cell().add(new Paragraph("TOTAL GÉNÉRAL").setBold()));
       recapTable.addCell(new Cell().add(new Paragraph(String.valueOf(dsCount + totalExams)).setBold()));

       document.add(new Paragraph("\nRécapitulatif des surveillances par type:\n").setBold());
       document.add(recapTable);
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

    private void addSurveillancesTable(Document document, Enseignant enseignant) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{20, 15, 15, 30, 20}));
        table.setWidth(UnitValue.createPercentValue(100));

        // En-têtes
        addCell(table, "Date", true);
        addCell(table, "Début", true);
        addCell(table, "Fin", true);
        addCell(table, "Matière", true);
        addCell(table, "Salle", true);

        // Données
        List<Surveillance> surveillances = surveillanceService.getSurveillancesByEnseignant(enseignant.getId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        for (Surveillance s : surveillances) {
            if (s.getStatut() == StatutSurveillance.PLANIFIEE) {
                addCell(table, dateFormat.format(s.getDateDebut()), false);
                addCell(table, timeFormat.format(s.getDateDebut()), false);
                addCell(table, timeFormat.format(s.getDateFin()), false);
                addCell(table, s.getMatiere().getNom(), false);
                addCell(table, s.getSalle().getNumero(), false);
            }
        }

        document.add(table);
    }

    private void addSummary(Document document, Enseignant enseignant) {
        List<Surveillance> surveillances = surveillanceService.getSurveillancesByEnseignant(enseignant.getId());
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
}