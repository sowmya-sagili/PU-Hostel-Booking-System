package com.parul.hostel.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.parul.hostel.entity.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfService {

    private final UploadService uploadService;

    public String generateBookingPdf(Booking booking) {
        String filename = "booking_" + booking.getId() + ".pdf";
        Path path = Paths.get(uploadService.getUploadDir()).resolve(filename);

        log.info("Generating PDF receipt for booking ID: {} at path: {}", booking.getId(), path.toAbsolutePath());

        try (Document document = new Document()) {
            PdfWriter.getInstance(document, new FileOutputStream(path.toFile()));
            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);

            // Title
            Paragraph title = new Paragraph("Hostel Booking Receipt", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Student details
            document.add(new Paragraph("Student Name: " + booking.getStudent().getName(), valueFont));
            document.add(new Paragraph("Email: " + booking.getStudent().getEmail(), valueFont));
            document.add(new Paragraph("Phone: " + booking.getStudent().getPhone(), valueFont));
            document.add(new Paragraph("Gender: " + booking.getStudent().getGender(), valueFont));
            
            document.add(new Paragraph("\nBooking Details:", labelFont));
            document.add(new Paragraph("Hostel: " + booking.getBed().getRoom().getFloor().getHostel().getName(), valueFont));
            document.add(new Paragraph("Room Number: " + booking.getBed().getRoom().getRoomNumber(), valueFont));
            document.add(new Paragraph("Bed Number: " + booking.getBed().getBedNumber(), valueFont));
            document.add(new Paragraph("Amount Paid: INR " + booking.getAmount(), valueFont));
            document.add(new Paragraph("Status: " + booking.getStatus().getValue(), valueFont));

            log.info("PDF receipt generated successfully: {}", filename);
            return filename;
        } catch (Exception e) {
            log.error("Failed to generate PDF for booking ID: {}", booking.getId(), e);
            throw new RuntimeException("Failed to generate PDF receipt", e);
        }
    }

    public byte[] generateBookingReceiptPdfBytes(Booking booking, String paymentFile) {
        log.info("Generating dynamic PDF receipt bytes for booking ID: {}", booking.getId());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (Document document = new Document()) {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(0, 74, 173));
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font boldBodyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, Color.GRAY);

            // University Header
            Paragraph header = new Paragraph("PARUL UNIVERSITY", headerFont);
            header.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(header);

            Paragraph subHeader = new Paragraph("P.O. Limda, Tal. Waghodia, Vadodara, Gujarat - 391760\nWeb: https://paruluniversity.ac.in", subHeaderFont);
            subHeader.setAlignment(Paragraph.ALIGN_CENTER);
            subHeader.setSpacingAfter(10);
            document.add(subHeader);

            Paragraph divider = new Paragraph("______________________________________________________________________________\n", subHeaderFont);
            divider.setAlignment(Paragraph.ALIGN_CENTER);
            divider.setSpacingAfter(15);
            document.add(divider);

            Paragraph receiptTitle = new Paragraph("HOSTEL BOOKING RECEIPT", titleFont);
            receiptTitle.setAlignment(Paragraph.ALIGN_CENTER);
            receiptTitle.setSpacingAfter(20);
            document.add(receiptTitle);

            // Table setup
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(20);
            table.setWidths(new float[]{35f, 65f});

            addTableCell(table, "Booking ID", String.valueOf(booking.getId()), boldBodyFont, bodyFont);
            addTableCell(table, "Booking Date / Time", booking.getBookedOn().toString(), boldBodyFont, bodyFont);
            addTableCell(table, "Booking Status", booking.getStatus().toString(), boldBodyFont, bodyFont);
            addTableCell(table, "Student Name", booking.getStudent().getName(), boldBodyFont, bodyFont);
            addTableCell(table, "Student Email", booking.getStudent().getEmail(), boldBodyFont, bodyFont);
            addTableCell(table, "Phone Number", booking.getStudent().getPhone() != null ? booking.getStudent().getPhone() : "N/A", boldBodyFont, bodyFont);
            addTableCell(table, "Gender", booking.getStudent().getGender() != null ? booking.getStudent().getGender() : "N/A", boldBodyFont, bodyFont);
            addTableCell(table, "Hostel Name", booking.getBed().getRoom().getFloor().getHostel().getName(), boldBodyFont, bodyFont);
            addTableCell(table, "Room Number", booking.getBed().getRoom().getRoomNumber(), boldBodyFont, bodyFont);
            addTableCell(table, "Bed Number", booking.getBed().getBedNumber(), boldBodyFont, bodyFont);
            addTableCell(table, "Amount Paid", "INR " + booking.getAmount(), boldBodyFont, bodyFont);
            addTableCell(table, "Payment Proof", paymentFile != null ? paymentFile : "N/A", boldBodyFont, bodyFont);

            document.add(table);

            // Footer
            Paragraph footer = new Paragraph("\n\nThis is a digitally generated receipt by the PU Hostel Booking System.", footerFont);
            footer.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate dynamic PDF bytes for booking ID: {}", booking.getId(), e);
            throw new RuntimeException("Failed to generate dynamic PDF receipt bytes", e);
        }
    }

    private void addTableCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell1 = new PdfPCell(new Paragraph(label, labelFont));
        cell1.setPadding(8);
        cell1.setBackgroundColor(new Color(245, 245, 245));
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Paragraph(value, valueFont));
        cell2.setPadding(8);
        table.addCell(cell2);
    }
}
