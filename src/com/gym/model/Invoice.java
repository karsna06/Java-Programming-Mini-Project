package com.gym.model;

import java.time.LocalDate;

/**
 * Represents a billing invoice generated for a member's subscription.
 * Must be generated before a payment can be recorded (Business Rule 3).
 */
public class Invoice {

    private static int idCounter = 1;

    public enum InvoiceStatus {
        UNPAID, PAID, PARTIALLY_PAID
    }

    private final int invoiceId;
    private final Member member;
    private final Plan plan;
    private final LocalDate issueDate;
    private LocalDate dueDate;

    private double subtotal;
    private double taxAmount;
    private double penaltyAmount;
    private double totalAmount;
    private double amountPaid;
    private InvoiceStatus status;

    private final boolean lateRenewal;
    private final String billingPeriod;

    public Invoice(Member member, Plan plan, boolean lateRenewal) {
        this.invoiceId     = idCounter++;
        this.member        = member;
        this.plan          = plan;
        this.issueDate     = LocalDate.now();
        this.dueDate       = LocalDate.now().plusDays(7);
        this.lateRenewal   = lateRenewal;
        this.billingPeriod = plan.getBillingCycle().name();
        this.amountPaid    = 0.0;
        this.status        = InvoiceStatus.UNPAID;

        calculateAmounts();
    }

    // ---------- Private ----------

    private void calculateAmounts() {
        this.subtotal      = plan.getBasePrice();
        this.taxAmount     = plan.getTaxAmount();
        this.penaltyAmount = lateRenewal ? plan.getLatePenaltyFee() : 0.0;
        this.totalAmount   = subtotal + taxAmount + penaltyAmount;
    }

    // ---------- Getters ----------

    public int getInvoiceId()          { return invoiceId; }
    public Member getMember()          { return member; }
    public Plan getPlan()              { return plan; }
    public LocalDate getIssueDate()    { return issueDate; }
    public LocalDate getDueDate()      { return dueDate; }
    public double getSubtotal()        { return subtotal; }
    public double getTaxAmount()       { return taxAmount; }
    public double getPenaltyAmount()   { return penaltyAmount; }
    public double getTotalAmount()     { return totalAmount; }
    public double getAmountPaid()      { return amountPaid; }
    public InvoiceStatus getStatus()   { return status; }
    public boolean isLateRenewal()     { return lateRenewal; }
    public String getBillingPeriod()   { return billingPeriod; }
    public double getBalance()         { return totalAmount - amountPaid; }

    public void setDueDate(LocalDate d){ this.dueDate = d; }

    // ---------- Business Methods ----------

    /**
     * Records a payment against this invoice and updates status.
     */
    public boolean applyPayment(double amount) {
        if (status == InvoiceStatus.PAID) {
            System.out.println("[!] Invoice #" + invoiceId + " is already fully paid.");
            return false;
        }
        if (amount <= 0) {
            System.out.println("[!] Payment amount must be positive.");
            return false;
        }
        double remaining = totalAmount - amountPaid;
        double applied   = Math.min(amount, remaining);
        amountPaid      += applied;

        if (amountPaid >= totalAmount) {
            status = InvoiceStatus.PAID;
        } else {
            status = InvoiceStatus.PARTIALLY_PAID;
        }
        return true;
    }

    @Override
    public String toString() {
        String separator = "+---------------------------------------------------------+";
        return String.format(
            "%s%n" +
            "| INVOICE #%-45d|%n" +
            "%s%n" +
            "| Member       : %-39s|%n" +
            "| Plan         : %-39s|%n" +
            "| Billing Cycle: %-39s|%n" +
            "| Issue Date   : %-39s|%n" +
            "| Due Date     : %-39s|%n" +
            "%s%n" +
            "| Subtotal     : Rs. %-35.2f|%n" +
            "| Tax (%.1f%%)   : Rs. %-35.2f|%n" +
            "| Late Penalty : Rs. %-35.2f|%n" +
            "|                                                         |%n" +
            "| TOTAL AMOUNT : Rs. %-35.2f|%n" +
            "| Amount Paid  : Rs. %-35.2f|%n" +
            "| Balance Due  : Rs. %-35.2f|%n" +
            "%s%n" +
            "| Status       : %-39s|%n" +
            "%s",
            separator,
            invoiceId,
            separator,
            member.getName() + " (ID: " + member.getMemberId() + ")",
            plan.getPlanName(),
            billingPeriod,
            issueDate,
            dueDate,
            separator,
            subtotal,
            plan.getTaxRate(), taxAmount,
            penaltyAmount,
            totalAmount,
            amountPaid,
            getBalance(),
            separator,
            status,
            separator
        );
    }
}
