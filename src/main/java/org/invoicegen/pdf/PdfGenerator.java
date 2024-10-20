package org.invoicegen.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.invoicegen.InvoiceData;
import org.invoicegen.InvoiceDataItem;
import org.invoicegen.csv.CsvProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Entities;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                dataModel.put("recipient_email", invoiceData.getCustomerEmail());
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
                String pdfFileName = "invoice_" + invoiceData.getNumber() + ".pdf";
                createPdfFromHtml(htmlContent, pdfFileName);


            } catch (TemplateException | IOException e) {
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

    private void createPdfFromHtml(String htmlContent, String pdfFileName) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(pdfFileName)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(outputStream);
            builder.run();
        }
    }

    public static void main(String[] args) {
        CsvProcessor cp = new CsvProcessor();
        PdfGenerator gen = new PdfGenerator();
        gen.generateInvoices(cp.parseCSV("src/main/resources/invoices.csv"));
    }
}