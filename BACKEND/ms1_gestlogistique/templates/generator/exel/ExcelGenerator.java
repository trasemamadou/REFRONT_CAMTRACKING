package com.groupeisi.ms1_gestschool.templates.generator.exel;



import com.groupeisi.ms1_gestschool.templates.DocumentFormat;
import com.groupeisi.ms1_gestschool.templates.generator.DocumentGenerator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.util.Map;

@Component
public class ExcelGenerator implements DocumentGenerator {

    @Override
    public byte[] generate(String htmlContent, Map<String, Object> data) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Document");
            Document doc = Jsoup.parse(htmlContent);

            // Style pour les en-têtes
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int rowNum = 0;
            Elements tables = doc.select("table");

            for (Element table : tables) {
                Elements rows = table.select("tr");

                for (Element row : rows) {
                    Row excelRow = sheet.createRow(rowNum++);
                    Elements cells = row.select("th, td");
                    int cellNum = 0;

                    for (Element cell : cells) {
                        Cell excelCell = excelRow.createCell(cellNum++);
                        excelCell.setCellValue(cell.text());

                        if (cell.tagName().equals("th")) {
                            excelCell.setCellStyle(headerStyle);
                        }
                    }
                }

                // Ligne vide entre les tables
                if (rowNum < tables.size()) {
                    rowNum++;
                }
            }

            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération Excel", e);
        }
    }

    @Override
    public DocumentFormat getFormat() {
        return DocumentFormat.EXCEL;
    }
}