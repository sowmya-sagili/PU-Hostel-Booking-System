package com.parul.hostel.service;

import com.parul.hostel.entity.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final UploadService uploadService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendBookingEmail(String toEmail, Booking booking, String action) {
        log.info("Attempting to send booking email notification to recipient: {}", toEmail);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            
            String subject;
            if ("approved".equalsIgnoreCase(action)) {
                subject = "Booking Approved - Hostel Allocation Confirmed";
            } else {
                subject = "Booking Cancelled - Hostel Booking Update";
            }
            helper.setSubject(subject);

            String htmlContent = buildHtmlContent(booking, action);
            helper.setText(htmlContent, true);

            if ("approved".equalsIgnoreCase(action)) {
                String pdfFilename = "booking_" + booking.getId() + ".pdf";
                Path pdfPath = Paths.get(uploadService.getUploadDir()).resolve(pdfFilename);
                File pdfFile = pdfPath.toFile();
                if (pdfFile.exists()) {
                    FileSystemResource fileResource = new FileSystemResource(pdfFile);
                    helper.addAttachment(pdfFilename, fileResource);
                    log.debug("PDF receipt attached to booking email: {}", pdfFilename);
                } else {
                    log.warn("PDF receipt file not found, sending email without attachment: {}", pdfPath);
                }
            }

            mailSender.send(message);
            log.info("Email sent successfully to recipient: {}", toEmail);

        } catch (Exception e) {
            log.error("Email sending failed for recipient: {}. Exception: {}", toEmail, e.getMessage(), e);
        }
    }

    private String buildHtmlContent(Booking booking, String action) {
        String studentName = booking.getStudent().getName();
        String hostelName = booking.getBed().getRoom().getFloor().getHostel().getName();
        String roomNo = booking.getBed().getRoom().getRoomNumber();
        String bedNo = booking.getBed().getBedNumber();
        String studentPhone = booking.getStudent().getPhone() != null ? booking.getStudent().getPhone() : "N/A";

        if ("approved".equalsIgnoreCase(action)) {
            return "<h2 style='color:#2E86C1;'>Hostel Booking Approved</h2>" +
                    "<p>Hello <b>" + studentName + "</b>,</p>" +
                    "<p>Your hostel booking has been <b style='color:green;'>approved</b>. Your payment has been received and verified successfully.</p>" +
                    "<h3>Booking Details</h3>" +
                    "<table border=\"1\" cellpadding=\"8\" style=\"border-collapse:collapse;\">" +
                    "<tr><td><b>Name</b></td><td>" + studentName + "</td></tr>" +
                    "<tr><td><b>Email</b></td><td>" + booking.getStudent().getEmail() + "</td></tr>" +
                    "<tr><td><b>Phone Number</b></td><td>" + studentPhone + "</td></tr>" +
                    "<tr><td><b>Hostel</b></td><td>" + hostelName + "</td></tr>" +
                    "<tr><td><b>Room</b></td><td>" + roomNo + "</td></tr>" +
                    "<tr><td><b>Bed</b></td><td>" + bedNo + "</td></tr>" +
                    "<tr><td><b>Payment Status</b></td><td style='color:green;'>Confirmed / Verified</td></tr>" +
                    "<tr><td><b>Booking Status</b></td><td style='font-weight:bold; color:green;'>APPROVED</td></tr>" +
                    "</table>" +
                    "<p>Your PDF receipt is attached below.</p>" +
                    "<p>Thank you!</p>";
        } else {
            return "<h2 style='color:red;'>Hostel Booking Cancelled</h2>" +
                    "<p>Hello <b>" + studentName + "</b>,</p>" +
                    "<p>Please be notified that your hostel booking has been <b style='color:red;'>cancelled</b>.</p>" +
                    "<p>As a result, your allocated bed has been released and made available for other students.</p>" +
                    "<h3>Booking Details</h3>" +
                    "<table border=\"1\" cellpadding=\"8\" style=\"border-collapse:collapse;\">" +
                    "<tr><td><b>Name</b></td><td>" + studentName + "</td></tr>" +
                    "<tr><td><b>Email</b></td><td>" + booking.getStudent().getEmail() + "</td></tr>" +
                    "<tr><td><b>Phone Number</b></td><td>" + studentPhone + "</td></tr>" +
                    "<tr><td><b>Hostel</b></td><td>" + hostelName + "</td></tr>" +
                    "<tr><td><b>Room</b></td><td>" + roomNo + "</td></tr>" +
                    "<tr><td><b>Bed</b></td><td>" + bedNo + " (Released)</td></tr>" +
                    "<tr><td><b>Booking Status</b></td><td style='font-weight:bold; color:red;'>CANCELLED</td></tr>" +
                    "</table>" +
                    "<p>If this is a mistake, please contact the admin.</p>";
        }
    }
}
