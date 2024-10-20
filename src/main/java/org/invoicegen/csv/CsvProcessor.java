package org.invoicegen.csv;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.invoicegen.InvoiceData;
import org.invoicegen.InvoiceDataItem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvProcessor {
    public List<InvoiceData> parseCSV(String csvFile) {
        List<InvoiceData> invoiceDataList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = line.split(",", -1);

                InvoiceData invoiceData = new InvoiceData();
                invoiceData.setNumber(fields[0].trim());
                invoiceData.setCustomerName(fields[1].trim());
                invoiceData.setCustomerAddress(fields[2].trim());
                invoiceData.setCustomerEmail(fields[3].trim());
                invoiceData.setTaxId(fields[4].trim());
                invoiceData.setInvoiceDate(fields[5].trim());

                List<InvoiceDataItem> items = new ArrayList<>();
                InvoiceDataItem item = new InvoiceDataItem(
                        fields[6].trim(),
                        Integer.parseInt(fields[7]),
                        Double.parseDouble(fields[8])
                );
                items.add(item);

                // Set items and total sum
                invoiceData.setItems(items);
                invoiceData.setTotalAmount(Double.parseDouble(fields[9].trim()));
                invoiceDataList.add(invoiceData);
            }
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }

        return invoiceDataList;
    }

    public static void main(String[] args) {
        CsvProcessor cp = new CsvProcessor();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(cp.parseCSV("src/main/resources/invoices.csv")));
    }
}
