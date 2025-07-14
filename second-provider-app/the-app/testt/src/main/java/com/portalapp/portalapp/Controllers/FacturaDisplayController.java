package com.portalapp.portalapp.Controllers;

import com.portalapp.portalapp.Model.Factura;
import com.portalapp.portalapp.Repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.pdf.PdfDocument;

@RestController
public class FacturaDisplayController {

    @Autowired
    private FacturaRepository facturaRepository;

    // Endpoint to fetch factura data by ID
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/factura/{billId}")
    public ResponseEntity<Factura> getFacturaById(@PathVariable Long billId) {
        Factura factura = facturaRepository.findById(billId).orElse(null);

        if (factura == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(factura);
    }

    // Endpoint to generate and download PDF for factura
    @CrossOrigin(origins = "http://localhost:3001")
    @GetMapping("/api/factura/download/{billId}")
    public ResponseEntity<byte[]> downloadFacturaPdf(@PathVariable Long billId) {
        Factura factura = facturaRepository.findById(billId).orElse(null);

        if (factura == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Adding content to the PDF
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

            // Prepare response with PDF content
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=factura.pdf");

            return ResponseEntity.ok().headers(headers).body(out.toByteArray());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
