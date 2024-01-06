/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.pdf;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import pl.polsl.skirentalservice.dto.PriceUnitsDto;
import pl.polsl.skirentalservice.pdf.dto.PdfEquipmentDataDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PdfHandler {
    private static final String FONT_PATH = "./font/Cambria-Font.ttf";

    private static final DeviceRgb SECONDARY_COLOR = new DeviceRgb(113, 128, 150);
    private static final DeviceRgb TABLE_HEADER_BGC = new DeviceRgb(221, 221, 221);

    private static final String[] TABLE_HEADERS = {
        "Nazwa sprzętu", "Ilość", "Cena netto", "Cena brutto", "Kaucja netto", "Kaucja brutto",
    };

    protected Document createDocument(PdfWriter pdfWriter) throws IOException {
        final PdfFont pdfFont = PdfFontFactory.createFont(FONT_PATH, PdfEncodings.CP1250);
        final PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);
        return new Document(pdfDocument).setFont(pdfFont).setFontSize(9);
    }

    protected Table generateHeader(String text, String identifier, String date) {
        final Table headerTable = flexTable(UnitValue.createPercentArray(new float[]{ 60, 40 }));
        headerTable.addCell(new Cell().add(new Paragraph("DOKUMENT\n" + text.toUpperCase())
            .setFontSize(13f).setFixedLeading(14f).setVerticalAlignment(VerticalAlignment.MIDDLE)).setBorder(Border.NO_BORDER));

        final Table headerLeftTable = flexTable(2);
        headerLeftTable.addCell(cellWithParagraph("Nr " + text + ":").setFontColor(SECONDARY_COLOR));
        headerLeftTable.addCell(cellWithParagraph(identifier));
        headerLeftTable.addCell(cellWithParagraph("Data " + text + ":").setFontColor(SECONDARY_COLOR));
        headerLeftTable.addCell(cellWithParagraph(date));

        headerTable.addCell(new Cell().add(headerLeftTable).setBorder(Border.NO_BORDER));
        return headerTable;
    }

    protected Table generateDetails(Map<String, String> leftDataCells, Map<String, String> rightDataCells, String header) {
        final Table documentDetails = flexTable(2);

        final Cell leftSide = new Cell().setBorder(Border.NO_BORDER).setMarginBottom(5f);
        leftSide.add(cellWithParagraph("Dane klienta", 10f));
        for (Map.Entry<String, String> cell : leftDataCells.entrySet()) {
            leftSide.add(dataCellHeader(cell.getKey() + ":"));
            leftSide.add(dataCellContent(cell.getValue()));
        }
        final Cell rightSide = new Cell().setBorder(Border.NO_BORDER).setMarginBottom(5f);
        rightSide.add(cellWithParagraph("Dane " + header, 10f));
        for (Map.Entry<String, String> cell : rightDataCells.entrySet()) {
            rightSide.add(dataCellHeader(cell.getKey() + ":"));
            rightSide.add(dataCellContent(cell.getValue()));
        }
        documentDetails.addCell(leftSide);
        documentDetails.addCell(rightSide);
        return documentDetails;
    }

    protected Table generateEquipments(List<PdfEquipmentDataDto> dataRows) {
        final Table equipments = flexTable(TABLE_HEADERS.length).setMarginBottom(10f);
        for (final String tableHeader : TABLE_HEADERS) {
            equipments.addCell(new Cell().add(new Paragraph(tableHeader)).setBackgroundColor(TABLE_HEADER_BGC));
        }
        for (final PdfEquipmentDataDto dataRow : dataRows) {
            equipments.addCell(dataTableCellContent(dataRow.getName()));
            equipments.addCell(dataTableCellContent(dataRow.getCount() + " szt."));
            equipments.addCell(dataTableCellContent(dataRow.getPriceNetto() + " zł"));
            equipments.addCell(dataTableCellContent(dataRow.getPriceBrutto() + " zł"));
            equipments.addCell(dataTableCellContent(dataRow.getDepositPriceNetto() + " zł"));
            equipments.addCell(dataTableCellContent(dataRow.getDepositPriceBrutto() + " zł"));
        }
        return equipments;
    }

    protected void generatePriceUnits(Document document, PriceUnitsDto priceUnits) {
        document.add(priceUnitBruttoParagraph("Cena za wypożyczenie (brutto): " + priceUnits.getTotalPriceBrutto() + " zł"));
        document.add(priceUnitNettoParagraph("(netto): " + priceUnits.getTotalPriceNetto() + " zł"));
        document.add(new Paragraph("\n").setFixedLeading(2f));
        document.add(priceUnitBruttoParagraph("+ kaucja (brutto): " + priceUnits.getTotalDepositPriceBrutto() + " zł"));
        document.add(priceUnitNettoParagraph("(netto): " + priceUnits.getTotalDepositPriceNetto() + " zł"));
    }

    protected void generateSummaryPrices(Document document, String totalSumBrutto, String totalSumNetto) {
        document.add(priceUnitBruttoParagraph("Łącznie (brutto): " + totalSumBrutto + " zł").setFontSize(12f).setBold());
        document.add(priceUnitNettoParagraph("(netto): " + totalSumNetto + " zł").setFontSize(10f).setBold());
    }

    protected void generateDescription(Document document, String text) {
        final Paragraph descriptionHeader = paragraphWithLeading("Dodatkowe uwagi:").setFontColor(SECONDARY_COLOR);
        final Paragraph description = paragraphWithLeading(text).setFontSize(10f);
        document.add(descriptionHeader);
        document.add(description);
    }

    protected Paragraph generateHeader(String text) {
        return new Paragraph(text).setFontSize(12f).setMarginBottom(10f);
    }

    protected Table generateHorizontalDivider() {
        final Table divider = flexTable(1);
        divider.setMargins(15f, 0, 15f, 0);
        divider.setBorder(new SolidBorder(SECONDARY_COLOR, .2f));
        return divider;
    }

    protected Table generatePricesDivider() {
        final Table dividerPrice = new Table(new float[]{ 150f }).setHorizontalAlignment(HorizontalAlignment.RIGHT);
        dividerPrice.setBorder(new SolidBorder(SECONDARY_COLOR, .2f));
        dividerPrice.setMargins(8f, 0, 8f, 0);
        return dividerPrice;
    }

    private Paragraph priceUnitBruttoParagraph(String text) {
        return new Paragraph(text).setFontSize(10f).setFixedLeading(5f).setTextAlignment(TextAlignment.RIGHT);
    }

    private Paragraph priceUnitNettoParagraph(String text) {
        return new Paragraph(text).setFixedLeading(5f).setFontColor(SECONDARY_COLOR).setTextAlignment(TextAlignment.RIGHT);
    }

    private Paragraph paragraphWithLeading(String text) {
        return new Paragraph(text).setFixedLeading(5f);
    }

    private Cell dataCellHeader(String text) {
        return new Cell().add(new Paragraph(text).setFixedLeading(9f).setMarginTop(8f))
            .setBorder(Border.NO_BORDER).setFontColor(SECONDARY_COLOR);
    }

    private Cell dataCellContent(String text) {
        return new Cell().add(new Paragraph(text).setFixedLeading(9f)).setBorder(Border.NO_BORDER).setFontSize(10f);
    }

    private Cell dataTableCellContent(String text) {
        return new Cell().add(new Paragraph(text).setFixedLeading(9f));
    }

    private Cell cellWithParagraph(String text, float fontSize) {
        return new Cell().add(new Paragraph(text)).setBorder(Border.NO_BORDER).setFontSize(fontSize);
    }

    private Cell cellWithParagraph(String text) {
        return cellWithParagraph(text, 9);
    }

    private Table flexTable(UnitValue[] unitValues) {
        return new Table(unitValues).useAllAvailableWidth();
    }

    private Table flexTable(int columns) {
        return new Table(columns).useAllAvailableWidth();
    }
}
