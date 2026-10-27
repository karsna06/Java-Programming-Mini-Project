package com.gym.model;

import java.time.LocalDate;

/**
 * Represents a payment made against an invoice.
 */
public class Payment {

    private static int idCounter = 1;

    public enum PaymentMethod {
        CASH, CARD, UPI, NET_BANKING, CHEQUE
    }

    private final int paymentId;
    private final Member member;
    private final Invoice invoice;
    private final double amount;
    private final PaymentMethod paymentMethod;
    private final LocalDate paymentDate;
    private final String referenceNumber;
    private final String remarks;

    public Payment(Member member, Invoice invoice, double amount,
                   PaymentMethod paymentMethod, String referenceNumber, String remarks) {
        this.paymentId       = idCounter++;
        this.member          = member;
        this.invoice         = invoice;
        this.amount          = amount;
        this.paymentMethod   = paymentMethod;
        this.paymentDate     = LocalDate.now();
        this.referenceNumber = referenceNumber;
        this.remarks         = remarks;
    }

    // ---------- Getters ----------

    public int getPaymentId()             { return paymentId; }
    public Member getMember()             { return member; }
    public Invoice getInvoice()           { return invoice; }
    public double getAmount()             { return amount; }
    public PaymentMethod getPaymentMethod(){ return paymentMethod; }
    public LocalDate getPaymentDate()     { return paymentDate; }
    public String getReferenceNumber()    { return referenceNumber; }
    public String getRemarks()            { return remarks; }

    @Override
    public String toString() {
        return String.format(
            "  [Payment #%d] Date: %s | Amount: Rs. %.2f | Method: %s | Ref: %s%n" +
            "               Invoice #%d | Member: %s | Remarks: %s",
            paymentId, paymentDate, amount, paymentMethod,
            (referenceNumber != null && !referenceNumber.isEmpty()) ? referenceNumber : "N/A",
            invoice.getInvoiceId(), member.getName(),
            (remarks != null && !remarks.isEmpty()) ? remarks : "-"
        );
    }
}
