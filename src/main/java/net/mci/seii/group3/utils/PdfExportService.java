package net.mci.seii.group3.utils;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import net.mci.seii.group3.model.Veranstaltung;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

public class PdfExportService {
    public static byte[] erzeugePdf(Veranstaltung v) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        doc.add(new Paragraph("Teilnehmerliste für: " + v.getName()));
        doc.add(new Paragraph("Startzeit: " + v.getStartzeit()));
        doc.add(new Paragraph(" "));

        for (String name : v.getTeilnehmer()) {
            LocalDateTime t = v.getTeilnahmen().get(name);
            String status = (t != null) ? "✓ " + t : "⏳ offen";
            doc.add(new Paragraph("- " + name + "  (" + status + ")"));
        }

        doc.close();
        return out.toByteArray();
    }
}
