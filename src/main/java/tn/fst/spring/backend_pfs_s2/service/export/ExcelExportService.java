package tn.fst.spring.backend_pfs_s2.service.export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.model.Surveillance;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public class ExcelExportService implements SurveillanceExportService {

    @Override
    public void export(List<Surveillance> surveillances, OutputStream outputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Surveillances");
            createHeaderRow(workbook, sheet);
            fillDataRows(sheet, surveillances);
            autoSizeColumns(sheet);
            workbook.write(outputStream);
        }
    }

    private void createHeaderRow(Workbook workbook, Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createHeaderStyle(workbook);

        String[] columns = {"ID", "Date Début", "Date Fin", "Session", "Matière",
                "Enseignant Principal", "Enseignant Secondaire", "Salle"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        return headerStyle;
    }

    private void fillDataRows(Sheet sheet, List<Surveillance> surveillances) {
        int rowNum = 1;
        for (Surveillance surveillance : surveillances) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, surveillance.getId());
            createCell(row, 1, ExportUtils.formatDate(surveillance.getDateDebut()));
            createCell(row, 2, ExportUtils.formatDate(surveillance.getDateFin()));
            createCell(row, 3, surveillance.getSessionExamen() != null ?
                    surveillance.getSessionExamen().getType().toString() : "");
            createCell(row, 4, surveillance.getMatiere() != null ?
                    surveillance.getMatiere().getNom() : "");
            createCell(row, 5, ExportUtils.formatEnseignant(surveillance.getEnseignantPrincipal()));
            createCell(row, 6, ExportUtils.formatEnseignant(surveillance.getEnseignantSecondaire()));
            createCell(row, 7, surveillance.getSalle() != null ?
                    surveillance.getSalle().getNumero() : "");
        }
    }

    private void createCell(Row row, int column, Object value) {
        Cell cell = row.createCell(column);
        if (value != null) {
            if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            } else {
                cell.setCellValue(value.toString());
            }
        }
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}