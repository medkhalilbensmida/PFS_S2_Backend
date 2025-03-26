package tn.fst.spring.backend_pfs_s2.service;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.model.*;
import tn.fst.spring.backend_pfs_s2.repository.*;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfGenerationService {

    @Autowired
    private SurveillanceRepository surveillanceRepository;

    @Autowired
    private EnseignantRepository enseignantRepository;

    public void generateConvocationPdf(Long enseignantId, OutputStream outputStream) throws IOException {
        Enseignant enseignant = enseignantRepository.findById(enseignantId)
                .orElseThrow(() -> new RuntimeException("Enseignant non trouvé"));

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        addHeader(document);
        addTitle(document);
        addTeacherInfo(document, enseignant);
        addSurveillancesTable(document, enseignant);
        //addSummaryTable(document, getSurveillancesForEnseignant(enseignantId));

        document.close();
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
        document.add(new Paragraph(String.format("\nA l'attention de Mr/Mme %s %s %s\n\n",
                enseignant.getNom(),
                enseignant.getPrenom()))
                .setFontSize(12));
    }

    private void addSurveillancesTable(Document document, Enseignant enseignant) {
        Table table = new Table(5).setWidth(UnitValue.createPercentValue(100));

        addHeaderCell(table, "Date");
        addHeaderCell(table, "Heure");
        addHeaderCell(table, "Salle");
        addHeaderCell(table, "Matière");
        addHeaderCell(table, "Niveau");

        List<Surveillance> surveillances = getSurveillancesForEnseignant(enseignant.getId());

        for (Surveillance s : surveillances) {
            String[] dateTime = formatDate(s.getDateDebut()).split(" ");
            addCell(table, dateTime[0]);
            addCell(table, dateTime[1]);
            addCell(table, s.getSalle().getNumero());
            addCell(table, s.getMatiere().getNom());
            addCell(table, s.getMatiere().getNiveau());
        }

        document.add(table);
    }

/*    private void addSummaryTable(Document document, List<Surveillance> surveillances) {
        document.add(new Paragraph("\nRécapitulatif des surveillances\n").setBold());

        long nbDS = surveillances.stream()
                .filter(s -> s.getSessionExamen().getType() == TypeSession.DS)
                .count();

        long nbPrepa = surveillances.stream()
                .filter(s -> s.getMatiere().getNiveau().toLowerCase().contains("prépa"))
                .count();

        long nbLIM = surveillances.stream()
                .filter(s -> {
                    String niveau = s.getMatiere().getNiveau().toLowerCase();
                    return niveau.contains("licence") ||
                            niveau.contains("ingénieur") ||
                            niveau.contains("master");
                })
                .count();

        Table recap = new Table(2).setWidth(UnitValue.createPercentValue(50));
        addHeaderCell(recap, "Type");
        addHeaderCell(recap, "Nombre");
        addCell(recap, "DS");
        addCell(recap, String.valueOf(nbDS));
        addCell(recap, "Classes Préparatoires");
        addCell(recap, String.valueOf(nbPrepa));
        addCell(recap, "Licence/Ingénieur/Master");
        addCell(recap, String.valueOf(nbLIM));
        addCell(recap, "Total");
        addCell(recap, String.valueOf(nbDS + nbPrepa + nbLIM));

        document.add(recap);
    }*/

    private List<Surveillance> getSurveillancesForEnseignant(Long enseignantId) {
        return surveillanceRepository.findAll().stream()
                .filter(s -> s.getStatut() == StatutSurveillance.PLANIFIEE &&
                        (s.getEnseignantPrincipal().getId().equals(enseignantId) ||
                                s.getEnseignantSecondaire().getId().equals(enseignantId)))
                .collect(Collectors.toList());
    }

    private void addHeaderCell(Table table, String text) {
        Cell cell = new Cell()
                .add(new Paragraph(text))
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(cell);
    }

    private void addCell(Table table, String text) {
        Cell cell = new Cell()
                .add(new Paragraph(text))
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(cell);
    }

    private String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(date);
    }
}