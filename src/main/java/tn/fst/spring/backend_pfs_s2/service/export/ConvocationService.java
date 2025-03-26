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

@Service
public class ConvocationService {

    @Autowired
    private SurveillanceService surveillanceService;

    @Autowired
    private EnseignantRepository enseignantRepository;

    public byte[] generateConvocation(Long enseignantId) throws Exception {
        Enseignant enseignant = enseignantRepository.findById(enseignantId)
                .orElseThrow(() -> new RuntimeException("Enseignant non trouvé"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // En-tête
        addHeader(document);

        // Titre
        addTitle(document);

        // Information de l'enseignant
        addTeacherInfo(document, enseignant);

        // Table des surveillances
        addSurveillancesTable(document, enseignant);

        // Récapitulatif
        addSummary(document, enseignant);

        document.close();
        return baos.toByteArray();
    }

    private void addHeader(Document document) {
        Paragraph header = new Paragraph()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12)
                .setBold();
        header.add("Université Tunis El Manar\n");
        header.add("Faculté des Sciences de Tunis\n");
        header.add("Département Sciences Informatiques\n");
        header.add("Année Universitaire 2024-2025\n\n");
        document.add(header);
    }

    private void addTitle(Document document) {
        document.add(new Paragraph("CONVOCATION DE SURVEILLANCE")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14)
                .setBold());
    }

    private void addTeacherInfo(Document document, Enseignant enseignant) {
        document.add(new Paragraph(String.format("\nA l'attention de %s %s %s\n\n",
                enseignant.getGrade() != null ? enseignant.getGrade() : "Mr/Mme",
                enseignant.getPrenom(),
                enseignant.getNom()))
                .setFontSize(12));

        document.add(new Paragraph("Vous êtes convoqué(e) pour assurer la surveillance des examens selon le planning suivant:\n\n"));
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