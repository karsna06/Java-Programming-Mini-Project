package com.gym.service;

import com.gym.model.*;
import com.gym.exception.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Core service layer that manages all gym data and business operations.
 */
public class GymManagementSystem {

    private final List<Member>  members  = new ArrayList<>();
    private final List<Plan>    plans    = new ArrayList<>();
    private final List<Payment> payments = new ArrayList<>();
    private final List<Trainer> trainers = new ArrayList<>();

    // ======================================================
    //  MEMBER OPERATIONS
    // ======================================================

    public void addMember(Member m) {
        members.add(m);
        System.out.println("[✔] Member added successfully: " + m.getName() + " (ID: " + m.getMemberId() + ")");
    }

    public Member findMemberById(int id) {
        return members.stream()
                .filter(m -> m.getMemberId() == id)
                .findFirst()
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + id + " not found."));
    }

    public List<Member> getAllMembers() {
        return Collections.unmodifiableList(members);
    }

    // ======================================================
    //  PLAN OPERATIONS
    // ======================================================

    public void addPlan(Plan p) {
        plans.add(p);
        System.out.println("[✔] Plan added: " + p.getPlanName() + " (ID: " + p.getPlanId() + ")");
    }

    public Plan findPlanById(int id) {
        return plans.stream()
                .filter(p -> p.getPlanId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Plan> getAllPlans() {
        return Collections.unmodifiableList(plans);
    }

    // ======================================================
    //  TRAINER OPERATIONS
    // ======================================================

    public void addTrainer(Trainer t) {
        trainers.add(t);
        System.out.println("[✔] Trainer added: " + t.getName());
    }

    public Trainer findTrainerById(int id) {
        return trainers.stream()
                .filter(t -> t.getTrainerId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Trainer> getAllTrainers() {
        return Collections.unmodifiableList(trainers);
    }

    // ======================================================
    //  SUBSCRIPTION OPERATIONS
    // ======================================================

    /**
     * Starts or renews a subscription for a member.
     * Business Rule 2: Dates updated immediately on activation/renewal.
     */
    public Subscription startOrRenewSubscription(Member member, Plan plan) {
        Subscription sub = member.getSubscription();

        if (sub == null) {
            // Brand-new subscription
            sub = new Subscription(member, plan);
            member.setSubscription(sub);
            sub.activate();
        } else {
            // Renewal
            boolean late = sub.isLateRenewal();
            if (late) {
                System.out.println("[!] Late renewal detected. Penalty fee of Rs."
                        + plan.getLatePenaltyFee() + " will be applied to invoice.");
            }
            sub.renew(plan);
        }
        return sub;
    }

    // ======================================================
    //  ATTENDANCE OPERATIONS
    // ======================================================

    /**
     * Records attendance for a member.
     * Business Rule 1: Active subscription required.
     */
    public AttendanceRecord recordAttendance(Member member, String notes) {
        if (!member.hasActiveSubscription()) {
            throw new InactiveSubscriptionException(
                "Member '" + member.getName() + "' does not have an active subscription. " +
                "Please start or renew the subscription first."
            );
        }
        AttendanceRecord record = new AttendanceRecord(member, notes);
        member.addAttendance(record);
        System.out.println("[✔] Attendance recorded for " + member.getName() + " on " + record.getDate());
        return record;
    }

    // ======================================================
    //  INVOICE OPERATIONS
    // ======================================================

    /**
     * Generates an invoice for a member based on their current subscription plan.
     * Business Rule 3: Invoice must be generated before payment.
     * Business Rule 4: Late renewal penalty applied automatically.
     */
    public Invoice generateInvoice(Member member) {
        Subscription sub = member.getSubscription();
        if (sub == null) {
            throw new InactiveSubscriptionException(
                "Cannot generate invoice: Member has no subscription on record."
            );
        }
        boolean late = sub.isLateRenewal();
        Invoice inv  = new Invoice(member, sub.getPlan(), late);
        member.addInvoice(inv);
        System.out.println("[✔] Invoice #" + inv.getInvoiceId() + " generated for " + member.getName());
        if (late) System.out.println("[!] Late renewal penalty of Rs."
                + sub.getPlan().getLatePenaltyFee() + " included.");
        return inv;
    }

    // ======================================================
    //  PAYMENT OPERATIONS
    // ======================================================

    /**
     * Records a payment against the member's latest unpaid invoice.
     * Business Rule 3: Invoice must exist.
     */
    public Payment recordPayment(Member member, double amount,
                                  Payment.PaymentMethod method,
                                  String reference, String remarks) {
        Invoice inv = member.getLatestUnpaidInvoice();
        if (inv == null) {
            throw new InvoiceNotFoundException(
                "No unpaid invoice found for member '" + member.getName() +
                "'. Please generate an invoice first."
            );
        }
        boolean applied = inv.applyPayment(amount);
        if (!applied) return null;

        Payment payment = new Payment(member, inv, amount, method, reference, remarks);
        payments.add(payment);
        System.out.printf("[✔] Payment of Rs. %.2f recorded for %s (Invoice #%d). Status: %s%n",
                amount, member.getName(), inv.getInvoiceId(), inv.getStatus());
        return payment;
    }

    public List<Payment> getAllPayments() {
        return Collections.unmodifiableList(payments);
    }
}

