package com.portalapp.portalapp.Controllers;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.portalapp.portalapp.Model.Factura;
import com.portalapp.portalapp.Repository.FacturaRepository;

import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class BillReceiverController {

    private String latestBillInfo = "No bill received yet.";

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; 

    @Autowired
    private FacturaRepository facturaRepository;


    @Autowired
    private SmsService smsService;

    // Endpoint to receive bill data and store it
    @PostMapping("/receivebill")
    public ResponseEntity<String> receiveBill(@RequestBody Factura receivedFactura) {
        try {
            Factura factura = new Factura();
            factura.setUsername(receivedFactura.getUsername());
            factura.setEmail(receivedFactura.getEmail());
            factura.setFirstname(receivedFactura.getFirstname());
            factura.setLastname(receivedFactura.getLastname());
            factura.setPhone(receivedFactura.getPhone());
            factura.setCreatedAt(receivedFactura.getCreatedAt());
            factura.setDeadline(receivedFactura.getDeadline());
            factura.setSum(receivedFactura.getSum());
            factura.setStatus(receivedFactura.getStatus());


            facturaRepository.save(factura);

            latestBillInfo = "Factura Noua! ID: " + factura.getId() + ", Username: " + factura.getUsername() ;

            //emailService.sendSimpleEmail(factura.getEmail(), "Factura noua !", latestBillInfo);

            // ............

            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(pdfOutputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Bill Details").setBold().setFontSize(16));
            document.add(new Paragraph("Username: " + factura.getUsername()));
            document.add(new Paragraph("Firstname: " + factura.getFirstname()));
            document.add(new Paragraph("Lastname: " + factura.getLastname()));
            document.add(new Paragraph("Email: " + factura.getEmail()));
            document.add(new Paragraph("Phone: " + factura.getPhone()));
            document.add(new Paragraph("Date: " + factura.getCreatedAt()));
            document.add(new Paragraph("Deadline: " + factura.getDeadline()));
            document.add(new Paragraph("Sum: " + factura.getSum()));
            document.add(new Paragraph("Status: " + factura.getStatus()));

            document.close();

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom("ozoneflo@gmail.com");
            helper.setTo(factura.getEmail());
            helper.setSubject("Factura noua!");
            helper.setText("Salut " + factura.getFirstname() + ",\n\nAi o factura noua. Detalii in atasament.");

            helper.addAttachment("factura.pdf", new ByteArrayResource(pdfOutputStream.toByteArray()));

            mailSender.send(mimeMessage);


            // ............

            String latestBillInfo2 = latestBillInfo + ", Platforma: Furnizor 1" + ", Suma: " + factura.getSum();

            messagingTemplate.convertAndSend("/topic/bills", latestBillInfo);
            System.out.println("WebSocket Notification Sent: " + latestBillInfo);

            smsService.createMsj(latestBillInfo2);

            return ResponseEntity.ok("Factura noua pentru " + receivedFactura.getUsername() + " stocata cu sucess.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the received bill.");
        }
    }

    // Endpoint to get bill information by bill ID
    @GetMapping("/factura/{billId}")
    public ResponseEntity<Factura> getBillInfo(@PathVariable Long billId) {
        try {
            Factura factura = facturaRepository.findById(billId).orElse(null);

            if (factura == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Bill not found
            }

            return ResponseEntity.ok(factura); // Return the bill data
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Error
        }
    }

    // Endpoint to download bill PDF (stubbed out here, implementation would need to be added)
    @GetMapping("/factura/download/{billId}")
    public ResponseEntity<String> downloadBillPdf(@PathVariable Long billId) {
        try {
            // Logic to generate and return the PDF (this could involve a service to generate the PDF)
            return ResponseEntity.ok("Download link for the bill with ID: " + billId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating the bill PDF.");
        }
    }
}
