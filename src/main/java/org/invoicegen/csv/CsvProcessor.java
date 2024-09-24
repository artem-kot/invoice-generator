package org.invoicegen.csv;

import com.opencsv.CSVReader;
import org.invoicegen.InvoiceData;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CsvProcessor {
    public List<InvoiceData> parseCSV(String csvFilePath) {
        List<InvoiceData> invoices = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] line;
            reader.readNext();
            while ((line = reader.readNext()) != null) {
                String number = line[0];
                String customerName = line[1];
                String customerAddress = line[2];
                String customerEmail = line[3];
                String taxId = line[4];
                String invoiceDate = line[5];
                String serviceProvided = line[6];
                int quantity = Integer.parseInt(line[7]);
                double price = Double.parseDouble(line[8]);
                double sum = Double.parseDouble(line[9]);
                String paymentMethod = line[10];

                InvoiceData invoice = new InvoiceData(number, customerName, customerAddress, customerEmail, taxId,
                        invoiceDate, serviceProvided, quantity, price, sum, paymentMethod);
                invoices.add(invoice);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return invoices;
    }
}
