package tn.fst.spring.backend_pfs_s2.service.export;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.model.Surveillance;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Service
public class CsvExportService implements SurveillanceExportService {

    @Override
    public void export(List<Surveillance> surveillances, OutputStream outputStream) throws IOException {
        // Write UTF-8 BOM for Excel compatibility
        outputStream.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

        CSVFormat csvFormat = CSVFormat.Builder.create()
                .setDelimiter(',')
                .setHeader("ID", "Date Début", "Date Fin", "Session", "Matière",
                        "Enseignant Principal", "Enseignant Secondaire", "Salle")
                .build();

        try (CSVPrinter csvPrinter = new CSVPrinter(
                new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), csvFormat)) {

            for (Surveillance surveillance : surveillances) {
                csvPrinter.printRecord(Arrays.asList(
                        surveillance.getId(),
                        ExportUtils.formatDate(surveillance.getDateDebut()),
                        ExportUtils.formatDate(surveillance.getDateFin()),
                        surveillance.getSessionExamen() != null ? surveillance.getSessionExamen().getType() : "",
                        surveillance.getMatiere() != null ? surveillance.getMatiere().getNom() : "",
                        ExportUtils.formatEnseignant(surveillance.getEnseignantPrincipal()),
                        ExportUtils.formatEnseignant(surveillance.getEnseignantSecondaire()),
                        surveillance.getSalle() != null ? surveillance.getSalle().getNumero() : ""
                ));
            }
            csvPrinter.flush();
        }
    }
}