package com.gym.model;

/**
 * Represents a gym membership plan with billing cycle, pricing, and penalty policy.
 */
public class Plan {

    private static int idCounter = 1;

    public enum BillingCycle {
        MONTHLY, QUARTERLY, HALF_YEARLY, YEARLY
    }

    private final int planId;
    private String planName;
    private BillingCycle billingCycle;
    private double basePrice;
    private double taxRate;           // percentage e.g. 18.0 means 18%
    private double latePenaltyFee;    // flat fee added on late renewal
    private int gracePeriodDays;      // days after expiry before penalty applies
    private String description;

    public Plan(String planName, BillingCycle billingCycle, double basePrice,
                double taxRate, double latePenaltyFee, int gracePeriodDays, String description) {
        this.planId          = idCounter++;
        this.planName        = planName;
        this.billingCycle    = billingCycle;
        this.basePrice       = basePrice;
        this.taxRate         = taxRate;
        this.latePenaltyFee  = latePenaltyFee;
        this.gracePeriodDays = gracePeriodDays;
        this.description     = description;
    }

    // ---------- Getters & Setters ----------

    public int getPlanId()                         { return planId; }
    public String getPlanName()                    { return planName; }
    public void setPlanName(String n)              { this.planName = n; }
    public BillingCycle getBillingCycle()          { return billingCycle; }
    public void setBillingCycle(BillingCycle bc)   { this.billingCycle = bc; }
    public double getBasePrice()                   { return basePrice; }
    public void setBasePrice(double p)             { this.basePrice = p; }
    public double getTaxRate()                     { return taxRate; }
    public void setTaxRate(double r)               { this.taxRate = r; }
    public double getLatePenaltyFee()              { return latePenaltyFee; }
    public void setLatePenaltyFee(double f)        { this.latePenaltyFee = f; }
    public int getGracePeriodDays()                { return gracePeriodDays; }
    public void setGracePeriodDays(int d)          { this.gracePeriodDays = d; }
    public String getDescription()                 { return description; }
    public void setDescription(String d)           { this.description = d; }

    // ---------- Computed ----------

    /** Returns cycle duration in months. */
    public int getCycleMonths() {
        return switch (billingCycle) {
            case MONTHLY     -> 1;
            case QUARTERLY   -> 3;
            case HALF_YEARLY -> 6;
            case YEARLY      -> 12;
        };
    }

    public double getTaxAmount() {
        return basePrice * taxRate / 100.0;
    }

    public double getTotalWithTax() {
        return basePrice + getTaxAmount();
    }

    @Override
    public String toString() {
        return String.format(
            "+---------------------------------------------------------+%n" +
            "| Plan ID      : %-39d|%n" +
            "| Plan Name    : %-39s|%n" +
            "| Billing Cycle: %-39s|%n" +
            "| Base Price   : Rs. %-35.2f|%n" +
            "| Tax Rate     : %-38s|%n" +
            "| Tax Amount   : Rs. %-35.2f|%n" +
            "| Total (w Tax): Rs. %-35.2f|%n" +
            "| Late Penalty : Rs. %-35.2f|%n" +
            "| Grace Period : %-38s|%n" +
            "| Description  : %-39s|%n" +
            "+---------------------------------------------------------+",
            planId, planName, billingCycle,
            basePrice,
            String.format("%.1f%%", taxRate),
            getTaxAmount(),
            getTotalWithTax(),
            latePenaltyFee,
            gracePeriodDays + " days",
            description
        );
    }
}
