
package com.gym.main;

import com.gym.model.*;
import com.gym.exception.*;
import com.gym.service.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * ╔══════════════════════════════════════════════════════════╗
 * ║       GYM MEMBERSHIP & BILLING MANAGEMENT SYSTEM        ║
 * ║            23CSR306 – Java OOP Mini Project              ║
 * ╚══════════════════════════════════════════════════════════╝
 *
 * Entry point – hosts the console menu and delegates to GymManagementSystem.
 *
 * Business Rules enforced:
 *  1. Active subscription required to record attendance.
 *  2. Subscription dates updated immediately on activation/renewal.
 *  3. Invoice must be generated before recording payment.
 *  4. Late renewal triggers penalty fee as per plan policy.
 *  5. Attendance records linked directly to members and dates.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final GymManagementSystem gym = new GymManagementSystem();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ════════════════════════════════════════════════════════
    //  ENTRY POINT
    // ════════════════════════════════════════════════════════

    public static void main(String[] args) {
        printBanner();
        seedDemoData();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            System.out.println();
            switch (choice) {
                case 1  -> handleAddMember();
                case 2  -> handleAddPlan();
                case 3  -> handleStartOrRenewSubscription();
                case 4  -> handleRecordAttendance();
                case 5  -> handleGenerateInvoice();
                case 6  -> handleRecordPayment();
                case 7  -> handleDisplayMembers();
                case 8  -> handleViewInvoice();
                case 9  -> handleAttendanceSummary();
                case 10 -> handleDisplayPlans();
                case 0  -> running = false;
                default -> System.out.println("[!] Invalid choice. Please try again.");
            }
        }
        System.out.println("\n  Thank you for using Gym Management System. Goodbye!");
        scanner.close();
    }

    // ════════════════════════════════════════════════════════
    //  MENU HANDLERS
    // ════════════════════════════════════════════════════════

    /** 1. Add a new Member */
    private static void handleAddMember() {
        printSectionHeader("ADD NEW MEMBER");

        String name  = readString("Enter Name           : ");
        String email = readString("Enter Email          : ");
        String phone = readString("Enter Phone          : ");
        LocalDate dob = readDate("Enter Date of Birth  : ");

        // Optional trainer assignment
        Trainer trainer = null;
        if (!gym.getAllTrainers().isEmpty()) {
            System.out.println("\n  Available Trainers:");
            for (Trainer t : gym.getAllTrainers()) System.out.println("  " + t);
            System.out.print("  Assign Trainer ID (0 to skip): ");
            int tid = readInt("");
            if (tid > 0) trainer = gym.findTrainerById(tid);
        }

        Member member = new Member(name, email, phone, dob);
        gym.addMember(member);
        if (trainer != null) trainer.assignMember(member);

        System.out.println();
        System.out.println(member);
    }

    /** 2. Add a new Plan */
    private static void handleAddPlan() {
        printSectionHeader("ADD NEW PLAN");

        String planName = readString("Plan Name              : ");

        System.out.println("  Billing Cycles:");
        for (Plan.BillingCycle bc : Plan.BillingCycle.values()) {
            System.out.printf("    [%d] %s%n", bc.ordinal() + 1, bc.name());
        }
        int cycleChoice = readInt("  Select Billing Cycle   : ") - 1;
        Plan.BillingCycle cycle = Plan.BillingCycle.values()[
                Math.max(0, Math.min(cycleChoice, Plan.BillingCycle.values().length - 1))];

        double basePrice      = readDouble("Base Price (Rs.)       : ");
        double taxRate        = readDouble("Tax Rate (%)           : ");
        double latePenaltyFee = readDouble("Late Penalty Fee (Rs.) : ");
        int gracePeriodDays   = readInt   ("Grace Period (days)    : ");
        String description    = readString("Description            : ");

        Plan plan = new Plan(planName, cycle, basePrice, taxRate, latePenaltyFee, gracePeriodDays, description);
        gym.addPlan(plan);

        System.out.println();
        System.out.println(plan);
    }

    /** 3. Start or Renew Subscription */
    private static void handleStartOrRenewSubscription() {
        printSectionHeader("START / RENEW SUBSCRIPTION");

        Member member = selectMember();
        if (member == null) return;

        Plan plan = selectPlan();
        if (plan == null) return;

        Subscription sub = gym.startOrRenewSubscription(member, plan);

        System.out.println("\n  ── Updated Subscription Details ──────────────────────");
        System.out.println(sub);
        System.out.println("  ─────────────────────────────────────────────────────\n");
        System.out.println("  [TIP] Generate an invoice now for this subscription.");
    }

    /** 4. Record Attendance */
    private static void handleRecordAttendance() {
        printSectionHeader("RECORD ATTENDANCE");

        Member member = selectMember();
        if (member == null) return;

        try {
            String notes = readString("Notes (optional)  : ");
            AttendanceRecord record = gym.recordAttendance(member, notes);
            System.out.println("\n  Attendance Entry:");
            System.out.println(record);
            System.out.printf("%n  Total sessions for %s: %d%n",
                    member.getName(), member.getTotalAttendance());
        } catch (InactiveSubscriptionException e) {
            System.out.println("\n  [ERROR] " + e.getMessage());
            System.out.println("  [TIP]  Use Option 3 to start/renew subscription first.");
        }
    }

    /** 5. Generate Invoice */
    private static void handleGenerateInvoice() {
        printSectionHeader("GENERATE INVOICE");

        Member member = selectMember();
        if (member == null) return;

        try {
            Invoice inv = gym.generateInvoice(member);
            System.out.println();
            System.out.println(inv);
        } catch (InactiveSubscriptionException e) {
            System.out.println("\n  [ERROR] " + e.getMessage());
        }
    }

    /** 6. Record Payment */
    private static void handleRecordPayment() {
        printSectionHeader("RECORD PAYMENT");

        Member member = selectMember();
        if (member == null) return;

        // Show pending invoice summary
        Invoice pending = member.getLatestUnpaidInvoice();
        if (pending == null) {
            System.out.println("\n  [!] No unpaid invoice found.");
            System.out.println("  [TIP] Use Option 5 to generate an invoice first.");
            return;
        }
        System.out.println("\n  Pending Invoice:");
        System.out.println(pending);

        double amount = readDouble("\nPayment Amount (Rs.) : ");

        System.out.println("  Payment Methods:");
        for (Payment.PaymentMethod pm : Payment.PaymentMethod.values()) {
            System.out.printf("    [%d] %s%n", pm.ordinal() + 1, pm.name());
        }
        int pmChoice = readInt("  Select Method       : ") - 1;
        Payment.PaymentMethod method = Payment.PaymentMethod.values()[
                Math.max(0, Math.min(pmChoice, Payment.PaymentMethod.values().length - 1))];

        String reference = readString("Reference Number    : ");
        String remarks   = readString("Remarks             : ");

        try {
            Payment payment = gym.recordPayment(member, amount, method, reference, remarks);
            if (payment != null) {
                System.out.println("\n  Payment Receipt:");
                System.out.println(payment);
                System.out.println("\n  Updated Invoice:");
                System.out.println(pending);
            }
        } catch (InvoiceNotFoundException e) {
            System.out.println("\n  [ERROR] " + e.getMessage());
        }
    }

    /** 7. Display All Members */
    private static void handleDisplayMembers() {
        printSectionHeader("ALL MEMBERS");

        List<Member> members = gym.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("  No members registered yet.");
            return;
        }
        for (Member m : members) {
            System.out.println(m);
            if (m.getSubscription() != null) {
                System.out.println("  ── Subscription ──────────────────────────────────────");
                System.out.println(m.getSubscription());
                System.out.println("  ─────────────────────────────────────────────────────");
            }
            System.out.println();
        }
    }

    /** 8. View Invoices for a member */
    private static void handleViewInvoice() {
        printSectionHeader("VIEW INVOICES");

        Member member = selectMember();
        if (member == null) return;

        List<Invoice> invoices = member.getInvoices();
        if (invoices.isEmpty()) {
            System.out.println("\n  No invoices found for " + member.getName() + ".");
            return;
        }
        System.out.printf("%n  Found %d invoice(s) for %s:%n%n",
                invoices.size(), member.getName());
        for (Invoice inv : invoices) {
            System.out.println(inv);
            System.out.println();
        }
    }

    /** 9. Attendance Summary */
    private static void handleAttendanceSummary() {
        printSectionHeader("ATTENDANCE SUMMARY");

        Member member = selectMember();
        if (member == null) return;

        List<AttendanceRecord> records = member.getAttendanceHistory();
        System.out.printf("%n  Attendance Summary for: %s (ID: %d)%n",
                member.getName(), member.getMemberId());
        System.out.println("  Total Sessions: " + records.size());
        System.out.println("  ─────────────────────────────────────────────────────");

        if (records.isEmpty()) {
            System.out.println("  No attendance records found.");
        } else {
            for (AttendanceRecord r : records) System.out.println(r);
        }
        System.out.println("  ─────────────────────────────────────────────────────");
    }

    /** 10. Display All Plans */
    private static void handleDisplayPlans() {
        printSectionHeader("ALL PLANS");

        List<Plan> plans = gym.getAllPlans();
        if (plans.isEmpty()) {
            System.out.println("  No plans available yet.");
            return;
        }
        for (Plan p : plans) {
            System.out.println(p);
            System.out.println();
        }
    }

    // ════════════════════════════════════════════════════════
    //  HELPER METHODS
    // ════════════════════════════════════════════════════════

    private static Member selectMember() {
        List<Member> members = gym.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("  [!] No members registered. Add a member first (Option 1).");
            return null;
        }
        System.out.println("  Available Members:");
        for (Member m : members) {
            System.out.printf("    [%d] %-25s | %s%n",
                m.getMemberId(), m.getName(),
                m.hasActiveSubscription() ? "Active" : "No Active Sub");
        }
        int id = readInt("  Enter Member ID: ");
        try {
            return gym.findMemberById(id);
        } catch (MemberNotFoundException e) {
            System.out.println("  [ERROR] " + e.getMessage());
            return null;
        }
    }

    private static Plan selectPlan() {
        List<Plan> plans = gym.getAllPlans();
        if (plans.isEmpty()) {
            System.out.println("  [!] No plans available. Add a plan first (Option 2).");
            return null;
        }
        System.out.println("  Available Plans:");
        for (Plan p : plans) {
            System.out.printf("    [%d] %-20s | %-12s | Rs. %.2f + %.1f%% tax%n",
                p.getPlanId(), p.getPlanName(), p.getBillingCycle(),
                p.getBasePrice(), p.getTaxRate());
        }
        int id = readInt("  Enter Plan ID: ");
        Plan plan = gym.findPlanById(id);
        if (plan == null) System.out.println("  [ERROR] Plan not found.");
        return plan;
    }

    // ════════════════════════════════════════════════════════
    //  UI UTILITIES
    // ════════════════════════════════════════════════════════

    private static void printBanner() {
        System.out.println("""
            +============================================================+
            |       GYM MEMBERSHIP & BILLING MANAGEMENT SYSTEM          |
            |            23CSR306 - Java OOP Mini Project                |
            +============================================================+
            """);
    }

    private static void printMenu() {
        System.out.println("""
            +-----------------------------------------------------------+
            |                      MAIN MENU                            |
            +-----------------------------------------------------------+
            |  [1]  Add Member                                          |
            |  [2]  Add Plan                                            |
            |  [3]  Start / Renew Subscription                          |
            |  [4]  Record Attendance                                   |
            |  [5]  Generate Invoice                                    |
            |  [6]  Record Payment                                      |
            |  [7]  Display All Members                                 |
            |  [8]  View Invoices (by Member)                           |
            |  [9]  Attendance Summary (by Member)                      |
            |  [10] Display All Plans                                   |
            |  [0]  Exit                                                |
            +-----------------------------------------------------------+""");
    }

    private static void printSectionHeader(String title) {
        String line = "=".repeat(61);
        System.out.println("\n+" + line + "+");
        System.out.printf( "|  %-59s|%n", title);
        System.out.println("+" + line + "+\n");
    }

    // ---------- Input Helpers ----------

    private static int readInt(String prompt) {
        while (true) {
            if (!prompt.isEmpty()) System.out.print("  " + prompt);
            try {
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a valid integer.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print("  " + prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a valid number.");
            }
        }
    }

    private static String readString(String prompt) {
        System.out.print("  " + prompt);
        return scanner.nextLine().trim();
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print("  " + prompt + " (yyyy-MM-dd): ");
            try {
                return LocalDate.parse(scanner.nextLine().trim(), DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("  [!] Invalid date format. Use yyyy-MM-dd.");
            }
        }
    }

    // ════════════════════════════════════════════════════════
    //  DEMO SEED DATA  (for quick testing)
    // ════════════════════════════════════════════════════════

    private static void seedDemoData() {
        System.out.println("  Loading demo data...\n");

        // --- Trainers ---
        Trainer t1 = new Trainer("Ravi Kumar",   "ravi@gym.com",   "9876543210",
                Trainer.Specialization.WEIGHT_TRAINING, 2000);
        Trainer t2 = new Trainer("Priya Sharma", "priya@gym.com",  "9876543211",
                Trainer.Specialization.YOGA, 1500);
        gym.addTrainer(t1);
        gym.addTrainer(t2);

        // --- Plans ---
        Plan basic    = new Plan("Basic Monthly",   Plan.BillingCycle.MONTHLY,
                1500, 18, 100, 5,  "Full gym access, no personal trainer");
        Plan premium  = new Plan("Premium Quarterly", Plan.BillingCycle.QUARTERLY,
                4000, 18, 200, 7,  "Full access + 1 PT session/week");
        Plan elite    = new Plan("Elite Yearly",    Plan.BillingCycle.YEARLY,
                12000, 18, 500, 10, "Unlimited access + daily PT + nutrition");
        gym.addPlan(basic);
        gym.addPlan(premium);
        gym.addPlan(elite);

        // --- Members ---
        Member m1 = new Member("Arjun Mehta",   "arjun@email.com",  "9001001001",
                LocalDate.of(1998, 5, 14));
        Member m2 = new Member("Sneha Pillai",  "sneha@email.com",  "9001001002",
                LocalDate.of(2000, 11, 22));
        Member m3 = new Member("Rohan Verma",   "rohan@email.com",  "9001001003",
                LocalDate.of(1995, 3, 8));
        gym.addMember(m1);
        gym.addMember(m2);
        gym.addMember(m3);

        t1.assignMember(m1);
        t1.assignMember(m3);
        t2.assignMember(m2);

        // --- Subscriptions ---
        gym.startOrRenewSubscription(m1, basic);
        gym.startOrRenewSubscription(m2, premium);
        // m3 has no active subscription intentionally

        // --- Attendance for m1 and m2 ---
        gym.recordAttendance(m1, "Morning session");
        gym.recordAttendance(m1, "Leg day");
        gym.recordAttendance(m2, "Yoga class");

        // --- Invoice + Payment for m1 ---
        Invoice inv1 = gym.generateInvoice(m1);
        gym.recordPayment(m1, inv1.getTotalAmount(),
                Payment.PaymentMethod.UPI, "UPI-REF-001", "Full payment via UPI");

        System.out.println("\n  ─── Demo data loaded. You can start from the menu. ───\n");
    }
}

