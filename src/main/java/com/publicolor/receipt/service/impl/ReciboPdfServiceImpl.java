package com.publicolor.receipt.service.impl;

import com.publicolor.receipt.dto.ReciboPdfItemRequest;
import com.publicolor.receipt.dto.ReciboPdfRequest;
import com.publicolor.receipt.service.ReciboPdfService;
import com.publicolor.shared.util.LogoUtil;
import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Genera el recibo de cobro en PDF, con tabla de conceptos igual que una factura —
 * pero el documento aclara en todo momento que NO es una factura, solo un cobro.
 *
 * Tamaño automático (el usuario no elige): por defecto una hoja chica de 13x21cm
 * horizontal (alcanza para pocos conceptos); si los conceptos no entran ahí, se
 * pasa solo a tamaño carta vertical.
 */
@Service
public class ReciboPdfServiceImpl implements ReciboPdfService {

    private static final Locale ES = Locale.forLanguageTag("es-CO");
    private static final float CM = 28.3465f;
    private static final Rectangle PAGINA_CHICA = new Rectangle(21 * CM, 13 * CM);
    private static final float MARGEN_CHICA = 22f;
    private static final float MARGEN_GRANDE = 36f;
    /** Alto aproximado que ocupan logo, encabezado de cliente/trabajo y totales, fuera de la tabla. */
    private static final float ALTO_RESERVADO = 215f;

    private static final Color BRAND_PURPLE = new Color(0x5A, 0x1F, 0xA3);
    private static final Color GRAY_HEADER = new Color(0xF1, 0xF5, 0xF9);
    private static final Color GREEN = new Color(0x05, 0x96, 0x69);
    private static final Color RED = new Color(0xE1, 0x1D, 0x48);

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

    private static final String DISCLAIMER =
            "Este documento corresponde a un cobro y no certifica la realización del pago. No es una factura.";

    @Override
    public byte[] generarPdf(ReciboPdfRequest req) {
        try {
            float anchoChica = PAGINA_CHICA.getWidth() - 2 * MARGEN_CHICA;
            PdfPTable tablaChica = construirTablaItems(req.getItems(), anchoChica);

            boolean cabeEnChica = tablaChica.getTotalHeight() <= (PAGINA_CHICA.getHeight() - 2 * MARGEN_CHICA - ALTO_RESERVADO);

            Rectangle pageSize = cabeEnChica ? PAGINA_CHICA : PageSize.LETTER;
            float margen = cabeEnChica ? MARGEN_CHICA : MARGEN_GRANDE;

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document(pageSize, margen, margen, margen, margen);
            PdfWriter.getInstance(doc, out);
            doc.open();

            doc.add(logo());
            doc.add(espacio(6));
            doc.add(tituloYDisclaimer());
            doc.add(espacio(8));
            doc.add(datosEncabezado(req));
            doc.add(espacio(8));

            float anchoFinal = pageSize.getWidth() - 2 * margen;
            PdfPTable tablaFinal = cabeEnChica ? tablaChica : construirTablaItems(req.getItems(), anchoFinal);
            doc.add(tablaFinal);

            doc.add(espacio(8));
            doc.add(totales(req));

            doc.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new IllegalStateException("No se pudo generar el PDF del recibo.", e);
        }
    }

    private Paragraph espacio(float pt) {
        Paragraph p = new Paragraph(" ");
        p.setSpacingAfter(0);
        p.setLeading(pt);
        return p;
    }

    private Element logo() {
        try {
            Image logo = Image.getInstance(LogoUtil.cargarLogo());
            logo.scaleToFit(140, 32);
            logo.setAlignment(Element.ALIGN_LEFT);
            return logo;
        } catch (Exception e) {
            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BRAND_PURPLE);
            return new Paragraph("Publicolor", font);
        }
    }

    private PdfPTable tituloYDisclaimer() throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.DARK_GRAY);
        PdfPCell tituloCell = new PdfPCell(new Phrase("RECIBO DE COBRO", tituloFont));
        tituloCell.setBorder(Rectangle.NO_BORDER);
        tituloCell.setPaddingBottom(2);
        table.addCell(tituloCell);

        Font disclaimerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY);
        PdfPCell disclaimerCell = new PdfPCell(new Phrase(DISCLAIMER, disclaimerFont));
        disclaimerCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(disclaimerCell);

        return table;
    }

    private PdfPTable datosEncabezado(ReciboPdfRequest req) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);

        table.addCell(celdaSinBorde("Recibo N.º " + req.getConsecutiveNumber() + "   ·   " + java.time.LocalDateTime.now().format(FECHA_HORA), labelFont, Element.ALIGN_LEFT));
        table.addCell(celdaSinBorde("Trabajo " + req.getJobCode(), labelFont, Element.ALIGN_RIGHT));

        PdfPCell cliente = new PdfPCell(new Phrase(req.getClientName(), valueFont));
        cliente.setColspan(2);
        cliente.setBorder(Rectangle.NO_BORDER);
        cliente.setPaddingTop(3);
        table.addCell(cliente);

        return table;
    }

    private PdfPCell celdaSinBorde(String texto, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(align);
        return cell;
    }

    private PdfPTable construirTablaItems(List<ReciboPdfItemRequest> items, float anchoDisponible) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table.setWidths(new float[]{6, 68, 26});
        table.setTotalWidth(anchoDisponible);
        table.setLockedWidth(true);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.DARK_GRAY);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        Font detalleFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 7.5f, Color.GRAY);

        addHeaderCell(table, "#", headerFont, Element.ALIGN_CENTER);
        addHeaderCell(table, "Concepto", headerFont, Element.ALIGN_LEFT);
        addHeaderCell(table, "Valor", headerFont, Element.ALIGN_RIGHT);
        table.setHeaderRows(1);
        table.setSplitRows(false);

        int n = 1;
        for (ReciboPdfItemRequest it : items) {
            addCell(table, String.valueOf(n++), cellFont, Element.ALIGN_CENTER);

            Paragraph concepto = new Paragraph();
            String titulo = it.getProductType() + (it.getDescription() != null && !it.getDescription().isBlank() ? " — " + it.getDescription() : "");
            concepto.add(new Chunk(titulo, cellFont));
            List<String> detalles = new java.util.ArrayList<>();
            if (it.getFinishes() != null) detalles.addAll(it.getFinishes());
            if (it.getLaminations() != null) detalles.addAll(it.getLaminations());
            if (!detalles.isEmpty()) {
                concepto.add(Chunk.NEWLINE);
                concepto.add(new Chunk(String.join(" · ", detalles), detalleFont));
            }
            if (it.getNotes() != null && !it.getNotes().isBlank()) {
                concepto.add(Chunk.NEWLINE);
                concepto.add(new Chunk(it.getNotes(), detalleFont));
            }
            PdfPCell conceptoCell = new PdfPCell(concepto);
            conceptoCell.setPadding(5);
            conceptoCell.setBorderColor(Color.LIGHT_GRAY);
            table.addCell(conceptoCell);

            addCell(table, formatearMoneda(it.getTotalAmount()), cellFont, Element.ALIGN_RIGHT);
        }

        if (items.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase("Sin conceptos.", cellFont));
            empty.setColspan(3);
            empty.setPadding(8);
            table.addCell(empty);
        }

        return table;
    }

    private Paragraph totales(ReciboPdfRequest req) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
        Font pendienteFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, RED);
        Font favorFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, GREEN);

        Paragraph p = new Paragraph();
        p.setAlignment(Element.ALIGN_RIGHT);
        p.add(new Chunk("Valor total: ", labelFont));
        p.add(new Chunk(formatearMoneda(req.getTotalAmount()) + "\n", valueFont));
        p.add(new Chunk("Total abonado: ", labelFont));
        p.add(new Chunk(formatearMoneda(req.getTotalPaid()) + "\n", valueFont));

        if (req.getCreditApplied() != null && req.getCreditApplied().compareTo(BigDecimal.ZERO) > 0) {
            p.add(new Chunk("Saldo a favor aplicado: ", labelFont));
            p.add(new Chunk(formatearMoneda(req.getCreditApplied()) + "\n", favorFont));
        }

        p.add(new Chunk("Saldo pendiente: ", labelFont));
        p.add(new Chunk(formatearMoneda(req.getPendingAmount()), pendienteFont));

        if (req.getRemainingCredit() != null && req.getRemainingCredit().compareTo(BigDecimal.ZERO) > 0) {
            p.add(new Chunk("\nSaldo a favor disponible: ", labelFont));
            p.add(new Chunk(formatearMoneda(req.getRemainingCredit()), favorFont));
        }

        return p;
    }

    private void addHeaderCell(PdfPTable table, String texto, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(GRAY_HEADER);
        cell.setPadding(5);
        cell.setHorizontalAlignment(align);
        cell.setBorderColor(Color.LIGHT_GRAY);
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String texto, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(texto == null ? "" : texto, font));
        cell.setPadding(5);
        cell.setHorizontalAlignment(align);
        cell.setBorderColor(Color.LIGHT_GRAY);
        table.addCell(cell);
    }

    private String formatearMoneda(BigDecimal valor) {
        if (valor == null) valor = BigDecimal.ZERO;
        return "$" + String.format(ES, "%,.0f", valor).replace(',', '.');
    }
}
