package com.gym.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a member's subscription to a plan.
 * Manages activation, renewal, expiry, and penalty detection.
 */
public class Subscription {

    private static int idCounter = 1;

    public enum SubscriptionStatus {
        ACTIVE, EXPIRED, PENDING
    }

    private final int subscriptionId;
    private final Member member;
    private Plan plan;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus status;
    private int renewalCount;

    public Subscription(Member member, Plan plan) {
        this.subscriptionId = idCounter++;
        this.member         = member;
        this.plan           = plan;
        this.status         = SubscriptionStatus.PENDING;
        this.renewalCount   = 0;
    }

    // ---------- Getters ----------

    public int getSubscriptionId()         { return subscriptionId; }
    public Member getMember()              { return member; }
    public Plan getPlan()                  { return plan; }
    public void setPlan(Plan plan)         { this.plan = plan; }
    public LocalDate getStartDate()        { return startDate; }
    public LocalDate getEndDate()          { return endDate; }
    public SubscriptionStatus getStatus()  { return status; }
    public int getRenewalCount()           { return renewalCount; }

    // ---------- Business Methods ----------

    /**
     * Activates a new subscription (first time). Start date = today.
     */
    public void activate() {
        this.startDate = LocalDate.now();
        this.endDate   = startDate.plusMonths(plan.getCycleMonths());
        this.status    = SubscriptionStatus.ACTIVE;
        System.out.println("[✔] Subscription activated: " + startDate + " → " + endDate);
    }

    /**
     * Renews an existing subscription. If expired, extends from today;
     * otherwise extends from current end date.
     */
    public void renew(Plan newPlan) {
        this.plan = newPlan;
        LocalDate base = (status == SubscriptionStatus.EXPIRED || endDate.isBefore(LocalDate.now()))
                ? LocalDate.now()
                : endDate;
        this.startDate  = base;
        this.endDate    = base.plusMonths(newPlan.getCycleMonths());
        this.status     = SubscriptionStatus.ACTIVE;
        this.renewalCount++;
        System.out.println("[✔] Subscription renewed: " + startDate + " → " + endDate);
    }

    /**
     * Checks and updates the subscription status based on today's date.
     */
    public void refreshStatus() {
        if (status == SubscriptionStatus.ACTIVE && endDate != null
                && LocalDate.now().isAfter(endDate)) {
            status = SubscriptionStatus.EXPIRED;
        }
    }

    public boolean isActive() {
        refreshStatus();
        return status == SubscriptionStatus.ACTIVE;
    }

    /**
     * Returns true if renewal is after grace period (late renewal = penalty applies).
     */
    public boolean isLateRenewal() {
        if (endDate == null) return false;
        long daysPastExpiry = ChronoUnit.DAYS.between(endDate, LocalDate.now());
        return daysPastExpiry > plan.getGracePeriodDays();
    }

    /**
     * Days until expiry (negative if already expired).
     */
    public long daysUntilExpiry() {
        if (endDate == null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }

    @Override
    public String toString() {
        refreshStatus();
        return String.format(
            "  Subscription ID : %d%n" +
            "  Plan            : %s (%s)%n" +
            "  Start Date      : %s%n" +
            "  End Date        : %s%n" +
            "  Status          : %s%n" +
            "  Renewals        : %d%n" +
            "  Days Remaining  : %d",
            subscriptionId,
            plan.getPlanName(), plan.getBillingCycle(),
            startDate != null ? startDate : "N/A",
            endDate   != null ? endDate   : "N/A",
            status,
            renewalCount,
            daysUntilExpiry()
        );
    }
}
