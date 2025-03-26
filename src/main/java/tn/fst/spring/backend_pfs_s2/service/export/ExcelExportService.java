package tn.fst.spring.backend_pfs_s2.service.export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.model.Surveillance;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public class ExcelExportService implements SurveillanceExportService {

    private static final String[] HEADERS = {
            "ID", "Date Début", "Date Fin", "Session", "Matière",
            "Enseignant Principal", "Enseignant Secondaire", "Salle"
    };


    @Override
    public void export(List<Surveillance> surveillances, OutputStream outputStream) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Surveillances");
            sheet.setDefaultColumnWidth(15);

            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataCellStyle = createDataCellStyle(workbook);
            // Remove this line as we'll create date styles per row
            // CellStyle dateStyle = createDateStyle(workbook);

            // Create header row
            createHeaderRow(sheet, headerStyle);

            // Fill data
            fillDataRows(sheet, surveillances, dataCellStyle);

            // Auto size and adjust columns
            adjustColumns(sheet);

            // Add filters
            sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, HEADERS.length - 1));

            workbook.write(outputStream);
        }
    }

    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();

        // Background color
        headerStyle.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Font
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);

        // Alignment
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Borders
        headerStyle.setBorderTop(BorderStyle.MEDIUM);
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setBorderLeft(BorderStyle.MEDIUM);
        headerStyle.setBorderRight(BorderStyle.MEDIUM);

        // Wrap text
        headerStyle.setWrapText(true);

        return headerStyle;
    }

    private CellStyle createDataCellStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // Borders
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // Alignment
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // Alternate row colors
        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return style;
    }


    private CellStyle createDateStyle(XSSFWorkbook workbook, boolean alternateRow) {
        CellStyle dateStyle = workbook.createCellStyle();

        // Copy all the basic cell properties
        dateStyle.setBorderTop(BorderStyle.THIN);
        dateStyle.setBorderBottom(BorderStyle.THIN);
        dateStyle.setBorderLeft(BorderStyle.THIN);
        dateStyle.setBorderRight(BorderStyle.THIN);
        dateStyle.setAlignment(HorizontalAlignment.CENTER);
        dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Set date format
        dateStyle.setDataFormat(workbook.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));

        // Set background color if it's an alternate row
        if (alternateRow) {
            dateStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            dateStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        return dateStyle;
    }

    private void createHeaderRow(XSSFSheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        headerRow.setHeight((short) 600);

        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
    }


    private void fillDataRows(XSSFSheet sheet, List<Surveillance> surveillances,
                              CellStyle dataCellStyle) {
        int rowNum = 1;
        for (Surveillance surveillance : surveillances) {
            Row row = sheet.createRow(rowNum++);
            row.setHeight((short) 500);

            boolean isAlternateRow = rowNum % 2 == 1;

            // Create row-specific styles
            CellStyle rowStyle = sheet.getWorkbook().createCellStyle();
            rowStyle.cloneStyleFrom(dataCellStyle);
            CellStyle rowDateStyle = createDateStyle(sheet.getWorkbook(), isAlternateRow);

            if (isAlternateRow) {
                rowStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                rowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }

            // Apply styles to cells
            createCell(row, 0, surveillance.getId(), rowStyle);
            createCell(row, 1, surveillance.getDateDebut(), rowDateStyle);
            createCell(row, 2, surveillance.getDateFin(), rowDateStyle);
            createCell(row, 3, surveillance.getSessionExamen() != null ?
                    surveillance.getSessionExamen().getType().toString() : "", rowStyle);
            createCell(row, 4, surveillance.getMatiere() != null ?
                    surveillance.getMatiere().getNom() : "", rowStyle);
            createCell(row, 5, ExportUtils.formatEnseignant(
                    surveillance.getEnseignantPrincipal()), rowStyle);
            createCell(row, 6, ExportUtils.formatEnseignant(
                    surveillance.getEnseignantSecondaire()), rowStyle);
            createCell(row, 7, surveillance.getSalle() != null ?
                    surveillance.getSalle().getNumero() : "", rowStyle);
        }
    }
    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellStyle(style);

        if (value != null) {
            if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            } else if (value instanceof java.util.Date) {
                cell.setCellValue((java.util.Date) value);
            } else {
                cell.setCellValue(value.toString());
            }
        }
    }

    private void adjustColumns(XSSFSheet sheet) {
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
            // Add some padding
            int currentWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, currentWidth + 1000);
        }
    }
}