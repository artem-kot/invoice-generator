package pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;

import static org.apache.pdfbox.pdmodel.font.PDType1Font.*;

public class PdfGenerator {

    public static void main(String[] args) {
        String[] names = {"Alice", "Bob", "Charlie", "David", "Eva"};

        for (String name : names) {
            try {
                generatePDF(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void generatePDF(String name) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        PDFont baseFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.beginText();
        contentStream.setFont(baseFont, 12);
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText("Hello, " + name + "! This is your dynamic PDF.");
        contentStream.endText();

        contentStream.close();
        document.save(name + "_dynamic_pdf.pdf");
        document.close();
    }
}
