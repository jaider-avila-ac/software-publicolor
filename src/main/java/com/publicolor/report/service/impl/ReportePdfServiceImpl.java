package com.publicolor.report.service.impl;

import org.openpdf.text.*;
import org.openpdf.text.pdf.ColumnText;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfPageEventHelper;
import org.openpdf.text.pdf.PdfWriter;
import com.publicolor.finance.expense.model.Egreso;
import com.publicolor.finance.expense.repository.EgresoRepository;
import com.publicolor.finance.income.model.IngresoManual;
import com.publicolor.finance.income.repository.IngresoManualRepository;
import com.publicolor.payment.model.OrigenPago;
import com.publicolor.payment.model.Pago;
import com.publicolor.payment.repository.PagoRepository;
import com.publicolor.report.dto.TipoReportePdf;
import com.publicolor.report.service.ReportePdfService;
import com.publicolor.shared.util.LogoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportePdfServiceImpl implements ReportePdfService {

    private static final Locale ES = Locale.forLanguageTag("es-CO");
    private static final DateTimeFormatter FECHA_CORTA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Color BRAND_PURPLE = new Color(0x5A, 0x1F, 0xA3);
    private static final Color GRAY_HEADER = new Color(0xF1, 0xF5, 0xF9);

    private final IngresoManualRepository ingresoRepo;
    private final EgresoRepository egresoRepo;
    private final PagoRepository pagoRepo;

    private record Movimiento(LocalDate fecha, String tipo, String codigo, String concepto, String categoria, BigDecimal valor) {
    }

    /** Numera cada página abajo a la derecha — útil cuando el reporte ocupa varias hojas. */
    private static class NumeroPaginaEvent extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            Font font = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
            ColumnText.showTextAligned(
                    writer.getDirectContent(),
                    Element.ALIGN_RIGHT,
                    new Phrase("Página " + writer.getPageNumber(), font),
                    document.right(),
                    document.bottom() - 20,
                    0);
        }
    }

    @Override
    public byte[] generarPdf(TipoReportePdf tipo, LocalDate from, LocalDate to) {
        List<Movimiento> movimientos = obtenerMovimientos(tipo, from, to);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.LETTER, 36, 36, 54, 36);
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            writer.setPageEvent(new NumeroPaginaEvent());
            doc.open();

            doc.add(logoOTitulo());
            doc.add(subtitulo(tipo, from, to));
            doc.add(new Paragraph(" "));

            doc.add(tablaMovimientos(tipo, movimientos));

            doc.add(new Paragraph(" "));
            doc.add(resumenTotales(tipo, movimientos));

            doc.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new IllegalStateException("No se pudo generar el PDF.", e);
        }
    }

    private List<Movimiento> obtenerMovimientos(TipoReportePdf tipo, LocalDate from, LocalDate to) {
        List<Movimiento> movimientos = new ArrayList<>();

        if (tipo == TipoReportePdf.INCOMES || tipo == TipoReportePdf.BOTH) {
            for (IngresoManual i : ingresoRepo.findByIncomeDateBetweenAndAnnulledFalseOrderByIncomeDateAsc(from, to)) {
                movimientos.add(new Movimiento(
                        i.getIncomeDate(), "Ingreso", i.getCode(), i.getConcept(),
                        i.getCategoria() == null ? "" : i.getCategoria().getName(), i.getAmount()));
            }
            // Los abonos a trabajos también son plata que entró — cuentan como ingreso acá igual
            // que en el dashboard. Se excluye el crédito auto-aplicado porque no es plata nueva.
            for (Pago p : pagoRepo.findByPaymentDateBetweenAndOriginAndAnnulledFalseOrderByPaymentDateAsc(from, to, OrigenPago.CASH)) {
                movimientos.add(new Movimiento(
                        p.getPaymentDate(), "Ingreso", p.getCode(),
                        "Abono — " + p.getTrabajo().getCode() + " · " + p.getTrabajo().getCliente().getName(),
                        "Abono a trabajo", p.getAmount()));
            }
        }
        if (tipo == TipoReportePdf.EXPENSES || tipo == TipoReportePdf.BOTH) {
            for (Egreso e : egresoRepo.findByExpenseDateBetweenAndAnnulledFalseOrderByExpenseDateAsc(from, to)) {
                movimientos.add(new Movimiento(
                        e.getExpenseDate(), "Egreso", e.getCode(), e.getConcept(),
                        e.getCategoria() == null ? "" : e.getCategoria().getName(), e.getAmount()));
            }
        }
        movimientos.sort((a, b) -> a.fecha().compareTo(b.fecha()));
        return movimientos;
    }

    /** Logo centrado arriba del reporte; si por algún motivo no carga, cae al título en texto. */
    private Element logoOTitulo() {
        try {
            Image logo = Image.getInstance(LogoUtil.cargarLogo());
            logo.scaleToFit(160, 40);
            logo.setAlignment(Element.ALIGN_CENTER);
            return logo;
        } catch (Exception e) {
            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BRAND_PURPLE);
            Paragraph p = new Paragraph("Publicolor", font);
            p.setAlignment(Element.ALIGN_CENTER);
            return p;
        }
    }

    private Paragraph subtitulo(TipoReportePdf tipo, LocalDate from, LocalDate to) {
        String etiquetaTipo = switch (tipo) {
            case INCOMES -> "Ingresos";
            case EXPENSES -> "Egresos";
            case BOTH -> "Ingresos y egresos";
        };
        String rango = from.equals(to)
                ? formatearFechaLarga(from)
                : formatearFechaLarga(from) + " – " + formatearFechaLarga(to);

        Font font = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.DARK_GRAY);
        Paragraph p = new Paragraph("Reporte de " + etiquetaTipo + " · " + rango, font);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(10);
        return p;
    }

    private String formatearFechaLarga(LocalDate date) {
        String mes = date.getMonth().getDisplayName(TextStyle.FULL, ES);
        return date.getDayOfMonth() + " de " + mes + " de " + date.getYear();
    }

    private PdfPTable tablaMovimientos(TipoReportePdf tipo, List<Movimiento> movimientos) throws DocumentException {
        boolean mostrarTipo = tipo == TipoReportePdf.BOTH;
        int columnas = mostrarTipo ? 6 : 5;
        PdfPTable table = new PdfPTable(columnas);
        table.setWidthPercentage(100);
        table.setWidths(mostrarTipo ? new float[]{11, 9, 13, 32, 17, 18} : new float[]{12, 15, 39, 17, 17});

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.DARK_GRAY);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

        addHeaderCell(table, "Fecha", headerFont);
        if (mostrarTipo) addHeaderCell(table, "Tipo", headerFont);
        addHeaderCell(table, "Código", headerFont);
        addHeaderCell(table, "Concepto", headerFont);
        addHeaderCell(table, "Categoría", headerFont);
        addHeaderCell(table, "Valor", headerFont);

        // El encabezado se repite en cada página nueva y ninguna fila se corte entre dos páginas.
        table.setHeaderRows(1);
        table.setSplitRows(false);

        for (Movimiento m : movimientos) {
            addCell(table, m.fecha().format(FECHA_CORTA), cellFont, Element.ALIGN_LEFT);
            if (mostrarTipo) addCell(table, m.tipo(), cellFont, Element.ALIGN_LEFT);
            addCell(table, m.codigo(), cellFont, Element.ALIGN_LEFT);
            addCell(table, m.concepto(), cellFont, Element.ALIGN_LEFT);
            addCell(table, m.categoria(), cellFont, Element.ALIGN_LEFT);
            addCell(table, formatearMoneda(m.valor()), cellFont, Element.ALIGN_RIGHT);
        }

        if (movimientos.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase("Sin movimientos en el rango seleccionado.", cellFont));
            empty.setColspan(columnas);
            empty.setPadding(8);
            empty.setBorderColor(Color.LIGHT_GRAY);
            table.addCell(empty);
        }

        return table;
    }

    private Paragraph resumenTotales(TipoReportePdf tipo, List<Movimiento> movimientos) {
        BigDecimal totalIngresos = sumarPorTipo(movimientos, "Ingreso");
        BigDecimal totalEgresos = sumarPorTipo(movimientos, "Egreso");

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK);
        Paragraph p = new Paragraph();
        p.setAlignment(Element.ALIGN_RIGHT);

        if (tipo == TipoReportePdf.INCOMES || tipo == TipoReportePdf.BOTH) {
            p.add(new Chunk("Total ingresos: " + formatearMoneda(totalIngresos) + "\n", font));
        }
        if (tipo == TipoReportePdf.EXPENSES || tipo == TipoReportePdf.BOTH) {
            p.add(new Chunk("Total egresos: " + formatearMoneda(totalEgresos) + "\n", font));
        }
        if (tipo == TipoReportePdf.BOTH) {
            p.add(new Chunk("Balance: " + formatearMoneda(totalIngresos.subtract(totalEgresos)), font));
        }
        return p;
    }

    private BigDecimal sumarPorTipo(List<Movimiento> movimientos, String tipo) {
        return movimientos.stream()
                .filter(m -> m.tipo().equals(tipo))
                .map(Movimiento::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String formatearMoneda(BigDecimal valor) {
        return "$" + String.format(ES, "%,.0f", valor).replace(',', '.');
    }

    private void addHeaderCell(PdfPTable table, String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(GRAY_HEADER);
        cell.setPadding(6);
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
}
