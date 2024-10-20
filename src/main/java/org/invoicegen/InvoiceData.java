package org.invoicegen;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class InvoiceData {
    private String number;
    private String customerName;
    private String customerAddress;
    private String customerEmail;
    private String taxId;
    private String invoiceDate;
    private String servicePeriodStart;
    private String servicePeriodEnd;
    private List<InvoiceDataItem> items;
    private double totalAmount;
    private final String iban = "DE12345678901234567890";
    private final String bic = "BANKDEFFXXX";

    public InvoiceData() {
    }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = transformDate(invoiceDate);
        setServicePeriodStart();
        setServicePeriodEnd();
    }

    public String getServicePeriodStart() { return servicePeriodStart; }
    public void setServicePeriodStart() {
        servicePeriodStart = transformDate(invoiceDate);
    }

    public String getServicePeriodEnd() { return servicePeriodEnd; }
    public void setServicePeriodEnd() {
        servicePeriodEnd = createServiceEndDate(invoiceDate);
    }

    public List<InvoiceDataItem> getItems() { return items; }
    public void setItems(List<InvoiceDataItem> items) { this.items = items; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getIban() { return iban; }
    public String getBic() { return bic; }
    private String transformDate(String inputDate) {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("d.M.yy");
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try {
            return LocalDate.parse(inputDate, inputFormat).format(outputFormat);
        } catch (DateTimeParseException e) {
            System.out.println("Couldn't process date: " + inputDate + "\n" + e.getMessage());
            return "";
        }
    }

    private String createServiceEndDate(String inputDate) {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("d.M.yy");
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try {
            LocalDate date = LocalDate.parse(inputDate, inputFormat);
            return date.plusDays(30).format(outputFormat);
        } catch (DateTimeParseException e) {
            System.out.println("Couldn't process date: " + inputDate + "\n" + e.getMessage());
            return "";
        }
    }
}
