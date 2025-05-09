package com.doctor.util;

import com.doctor.entity.Appointment;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportUtil {
    public static void exportToPDF(List<Appointment> appointments, String filePath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Appointment Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Create table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            
            // Add headers
            addTableHeader(table);
            
            // Add data
            for (Appointment appointment : appointments) {
                addTableData(table, appointment);
            }

            document.add(table);
            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportToExcel(List<Appointment> appointments, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Appointments");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Doctor", "Patient", "Date/Time", "Status"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Add data
            int rowNum = 1;
            for (Appointment appointment : appointments) {
                Row row = sheet.createRow(rowNum++);
                addExcelRow(row, appointment);
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addTableHeader(PdfPTable table) {
        String[] headers = {"ID", "Doctor", "Patient", "Date/Time", "Status"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }
    }

    private static void addTableData(PdfPTable table, Appointment appointment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA);
        
        PdfPCell cell1 = new PdfPCell(new Phrase(appointment.getId().toString(), normalFont));
        PdfPCell cell2 = new PdfPCell(new Phrase(appointment.getDoctor().getName(), normalFont));
        PdfPCell cell3 = new PdfPCell(new Phrase(appointment.getPatient().getName(), normalFont));
        PdfPCell cell4 = new PdfPCell(new Phrase(appointment.getAppointmentDateTime().format(formatter), normalFont));
        PdfPCell cell5 = new PdfPCell(new Phrase(appointment.getStatus(), normalFont));

        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell5.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        table.addCell(cell5);
    }

    private static void addExcelRow(Row row, Appointment appointment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Cell cell1 = row.createCell(0);
        cell1.setCellValue(appointment.getId());
        
        Cell cell2 = row.createCell(1);
        cell2.setCellValue(appointment.getDoctor().getName());
        
        Cell cell3 = row.createCell(2);
        cell3.setCellValue(appointment.getPatient().getName());
        
        Cell cell4 = row.createCell(3);
        cell4.setCellValue(appointment.getAppointmentDateTime().format(formatter));
        
        Cell cell5 = row.createCell(4);
        cell5.setCellValue(appointment.getStatus());
    }
}