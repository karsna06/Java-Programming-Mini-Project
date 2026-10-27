package com.gym.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a gym member with personal details, subscription, and attendance history.
 */
public class Member {

    private static int idCounter = 1;

    private final int memberId;
    private String name;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private final LocalDate joinDate;
    private Subscription subscription;
    private final List<AttendanceRecord> attendanceHistory;
    private final List<Invoice> invoices;
    private Trainer assignedTrainer;

    public Member(String name, String email, String phone, LocalDate dateOfBirth) {
        this.memberId      = idCounter++;
        this.name          = name;
        this.email         = email;
        this.phone         = phone;
        this.dateOfBirth   = dateOfBirth;
        this.joinDate      = LocalDate.now();
        this.attendanceHistory = new ArrayList<>();
        this.invoices      = new ArrayList<>();
    }

    // ---------- Getters & Setters ----------

    public int getMemberId()             { return memberId; }
    public String getName()              { return name; }
    public void setName(String name)     { this.name = name; }
    public String getEmail()             { return email; }
    public void setEmail(String email)   { this.email = email; }
    public String getPhone()             { return phone; }
    public void setPhone(String phone)   { this.phone = phone; }
    public LocalDate getDateOfBirth()    { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dob) { this.dateOfBirth = dob; }
    public LocalDate getJoinDate()       { return joinDate; }
    public Subscription getSubscription(){ return subscription; }
    public void setSubscription(Subscription s) { this.subscription = s; }
    public List<AttendanceRecord> getAttendanceHistory() { return attendanceHistory; }
    public List<Invoice> getInvoices()   { return invoices; }
    public Trainer getAssignedTrainer()  { return assignedTrainer; }
    public void setAssignedTrainer(Trainer t) { this.assignedTrainer = t; }

    // ---------- Business Methods ----------

    public boolean hasActiveSubscription() {
        return subscription != null && subscription.isActive();
    }

    public void addAttendance(AttendanceRecord record) {
        attendanceHistory.add(record);
    }

    public void addInvoice(Invoice invoice) {
        invoices.add(invoice);
    }

    public int getTotalAttendance() {
        return attendanceHistory.size();
    }

    public Invoice getLatestUnpaidInvoice() {
        for (int i = invoices.size() - 1; i >= 0; i--) {
            if (invoices.get(i).getStatus() == Invoice.InvoiceStatus.UNPAID) {
                return invoices.get(i);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String trainerInfo = (assignedTrainer != null) ? assignedTrainer.getName() : "None";
        String subStatus   = hasActiveSubscription()
                ? "Active (Plan: " + subscription.getPlan().getPlanName() + ")"
                : "No Active Subscription";
        return String.format(
            "+---------------------------------------------------------+%n" +
            "| Member ID   : %-40d|%n" +
            "| Name        : %-40s|%n" +
            "| Email       : %-40s|%n" +
            "| Phone       : %-40s|%n" +
            "| DOB         : %-40s|%n" +
            "| Join Date   : %-40s|%n" +
            "| Trainer     : %-40s|%n" +
            "| Subscription: %-40s|%n" +
            "| Attendance  : %-40d|%n" +
            "+---------------------------------------------------------+",
            memberId, name, email, phone, dateOfBirth, joinDate,
            trainerInfo, subStatus, getTotalAttendance()
        );
    }
}
