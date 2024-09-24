package org.invoicegen.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Document;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import freemarker.template.*;
import org.invoicegen.InvoiceData;
import org.invoicegen.InvoiceDataItem;
import org.invoicegen.csv.CsvProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Entities;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class PdfGenerator {
    private Configuration freemarkerConfig;

    public PdfGenerator() {
        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_30);
        freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates");
    }

    public void generateInvoices(List<InvoiceData> invoiceDataList) {
        for (InvoiceData invoiceData : invoiceDataList) {
            try {
                Template template = freemarkerConfig.getTemplate("invoice_template.html");
                Map<String, Object> dataModel = new HashMap<>();
                dataModel.put("recipient_name", invoiceData.getCustomerName());
                dataModel.put("recipient_address", invoiceData.getCustomerAddress());
                dataModel.put("invoice_number", invoiceData.getNumber());
                dataModel.put("invoice_date", invoiceData.getInvoiceDate());
                dataModel.put("tax_number", invoiceData.getTaxId());
                dataModel.put("service_period_start", invoiceData.getServicePeriodStart());
                dataModel.put("service_period_end", invoiceData.getServicePeriodEnd());

                // Prepare list of items for the invoice
                List<Map<String, Object>> itemsList = new ArrayList<>();
                for (InvoiceDataItem item : invoiceData.getItems()) {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("description", item.getDescription());
                    itemMap.put("quantity", item.getQuantity());
                    itemMap.put("unit_price", item.getUnitPrice());
                    itemMap.put("total_price", item.getTotalPrice());
                    itemsList.add(itemMap);
                }
                dataModel.put("items", itemsList);

                dataModel.put("total_amount", invoiceData.getTotalAmount());
                dataModel.put("iban", invoiceData.getIban());
                dataModel.put("bic", invoiceData.getBic());

                StringWriter htmlWriter = new StringWriter();
                template.process(dataModel, htmlWriter);
                String htmlContent = htmlWriter.toString();

                htmlContent = cleanHtml(htmlContent);

                String pdfFileName = "invoice_" + invoiceData.getNumber() + ".pdf";
                createPdfFromHtml(htmlContent, pdfFileName);


            } catch (TemplateException | IOException | DocumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private String cleanHtml(String html) {
        org.jsoup.nodes.Document doc = Jsoup.parse(html);
        doc.outputSettings(new org.jsoup.nodes.Document.OutputSettings().prettyPrint(false));
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        return doc.html();
    }

    private void createPdfFromHtml(String htmlContent, String pdfFileName) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFileName));
        document.open();

        InputStream htmlStream = new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8));
        InputStreamReader htmlReader = new InputStreamReader(htmlStream, StandardCharsets.UTF_8);

        HTMLWorker htmlWorker = new HTMLWorker(document);
        htmlWorker.parse(htmlReader);

        document.close();
        writer.close();
    }

    public static void main(String[] args) {
        CsvProcessor cp = new CsvProcessor();
        PdfGenerator gen = new PdfGenerator();
        gen.generateInvoices(cp.parseCSV("src/main/resources/invoices.csv"));
    }
}