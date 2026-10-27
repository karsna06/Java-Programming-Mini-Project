# 23CSR306 JAVA PROGRAMMING - ASSIGNMENT II: OOP MINI PROJECT

## Academic Year: 2026-2027 | Department of Computer Science and Engineering

### Karpagam College of Engineering (Autonomous), Coimbatore - 641032

\---

### Student Details

* **Student Name:** KARSNA N
* **Roll Number:** 717825I224
* **Register Number:** 717825I224
* **Year / Semester / Section:** II / III / B
* **Department:** Artificial Intelligence and Data Science (AD)
* **Course Code \& Title:** 23CSR306 - Java Programming
* **Date of Submission:** 25-10-2026
* **Repository URL:** https://github.com/karsna06/Java-Programming-Mini-Project

### 1.1 Problem Statement



&#x20;Design and implement a Java console application for a Gym Membership \& Billing Management System that handles memberships, subscription plans, member check-ins/attendance tracking, automated tax and penalty invoicing, and payment processing. The application demonstrates core object-oriented programming principles (encapsulation, abstraction, association, aggregation, and composition) and enforces strict business rules regarding active subscriptions, immediate validity window updates, prior-invoice payment prerequisites, late penalty calculation, and direct audit linking.



### 1.2 Class Requirements



* Member
* Plan 
* Subscription 
* AttendanceRecord 
* Invoice 
* Payment 
* Trainer 

(Additional helper classes implemented: GymManagementSystem, custom domain exceptions InactiveSubscriptionException, InvoiceNotFoundException, MemberNotFoundException, and interactive console controller Main)



### 1.3 Business Rules Implemented



1. **Active Subscription Required to Record Attendance:** A member must hold an active subscription to record attendance. If a member with an expired or non-existent subscription attempts to check in, the system aborts with an InactiveSubscriptionException and no attendance record is saved.
2. **Immediate Validity Date Updates:** Subscription activation and renewal calculate and update start and end validity dates immediately based on the selected plan's billing cycle (Monthly, Quarterly, Half-Yearly, Yearly).
3. **Invoices Generated Prior to Payment Acceptance:** Invoices detailing subtotal, applicable taxes (GST), and late renewal penalties must be explicitly generated prior to accepting or recording payments against a membership.
4. **Late Renewals Penalty Calculation:** Subscriptions renewed beyond the plan's defined grace period automatically incur a late penalty fee according to the plan's policy, computed and itemized on the resulting invoice.
5. **Direct Member \& Date Linkage:** Every AttendanceRecord maintains direct references to its associated Member object and timestamps both check-in and optional check-out instances.



### 1.4 Menu Options



* Add Member 
* Add Plan Start or Renew Subscription 
* Record Attendance 
* Generate Invoice 
* Record Payment 
* Display Members 
* Exit 

(Additional management options provided: View Invoice Details, Attendance Summary, Display Plans)





## 2\. Object-Oriented Programming (OOP) Architecture

|OOP Principle|Implementation Details in Codebase|
|-|-|
|**Encapsulation**| All model entity state variables (Member, Plan, Subscription, Invoice, Payment, AttendanceRecord, Trainer) are declared private with explicit, validated public getters and setters. Data mutations are guarded to prevent inconsistent state.|
|**Inheritance**|PremiumMember extends Member, reusing the base profile attributes (memberId, name, contactNumber, email) and adding specialized attributes (rewardPoints, membershipTier).|
|**Polymorphism**|Member defines calculateDiscount(double spentBeforeTax), calculatePointsEarned(double amount) and redeemPointsForDiscount(double discount). PremiumMember overrides these to apply the ₹10 per 100 points discount rule and the 1 point per ₹100 accrual rule. Dynamic method dispatch handles billing without instanceof checks.|
|**Aggregation**|Invoice and Payment maintain direct references to Member and Plan objects. AttendanceRecord aggregates its parent Member instance, allowing independent entity lifecycles. |
|**Composition**|Member tightly encapsulates and manages the lifecycles of its Subscription, list of AttendanceRecord entries, and list of Invoice records. |
|**Exception Handling**| Custom unchecked/checked business exceptions (InactiveSubscriptionException, InvoiceNotFoundException, MemberNotFoundException) ensure robust operational flow and prevent invalid transactions without runtime crashes.|



## 3\. Package Structure



├── .gitignore                               # Git ignore configuration

├── README.md                                # Project documentation and academic report

├── run.bat                                  # Windows batch execution script

├── AttendanceRecord.java                    # Check-in timestamp and member attendance entity

├── GymManagementSystem.java                 # Service layer managing members, plans, \& operations

├── InactiveSubscriptionException.java       # Custom exception for attendance without active plan

├── Invoice.java                             # Invoice entity with tax, penalty, and status tracking

├── InvoiceNotFoundException.java            # Custom exception for missing invoice lookup

├── Main.java                                # Interactive CLI runner and seed demo orchestrator

├── Member.java                              # Gym member entity with profile, trainer, and records

├── MemberNotFoundException.java             # Custom exception for invalid member queries

├── Payment.java                             # Payment receipt entity linked to invoice

├── Plan.java                                # Plan entity with billing cycle, tax rate, and penalty

├── Subscription.java                        # Subscription validity lifecycle and status tracking

├── Trainer.java                             # Fitness trainer entity with specializations

└── out/                                     # Compiled bytecode (.class files)

## 4\. How to Compile and Run

### Method 1: Using Command Line / Terminal

Ensure Java JDK 17 or higher (e.g. Microsoft OpenJDK 21 / Eclipse Temurin) is installed.

Open terminal in the project directory:

```bash

&#x20;  cd "c:\\Users\\Karsna N\\Downloads\\23CSR306\_Java\_OOP\_Mini\_Project\\src\\com\\inventory\\exception\\Gym membership and billing"

```

Compile all source files into the `out` directory:

```bash

&#x20;  javac -d out -sourcepath src src/com/gym/*/*.java

```

Run the main application:

```bash

&#x20;  java -cp out com.gym.main.Main

```

### Method 2: In Eclipse IDE

1. Open Eclipse IDE.
2. Select **File -> Import... -> General -> Existing Projects into Workspace**.
3. Browse and select the project directory: `c:\\Users\\tcgme\\Downloads\\java proj`.
4. Click **Finish**.
5. Locate `src/com/inventory/main/GymMembershipAndBilling.java`.
6. Right-click and choose **Run As -> Java Application**.

\---

## 5\. Sample Execution Scenarios

### Scenario A:  Display Members and Plans Catalog

Displays all registered members (`Arjun Mehta`, `Sneha Pillai`, `Rohan Verma`), their subscription statuses (`ACTIVE`, `PENDING`), assigned trainers (`Ravi Kumar`, `Priya Sharma`), and plan details (`Basic Monthly`, `Premium Quarterly`, `Elite Yearly`).

### Scenario B: Subscribing / Renewing Member (Start or Renew Subscription)

Member `Arjun Mehta` activates the `Basic Monthly` plan (Base Price: ₹1,500, Tax: 18%). The system immediately calculates and sets validity dates from current date to one month ahead (`2026-10-28` to `2026-11-28`), transitioning status to `ACTIVE`.

### Scenario C: Generating Invoice \& Payment Processing

An invoice is generated for `Arjun Mehta` prior to accepting payment:

* Subtotal: ₹1,500.00
* GST (18%): ₹270.00
* Late Penalty: ₹0.00
* Total Invoice Payable: ₹1,770.00

Payment of ₹1,770.00 is recorded via `UPI` (Ref: `UPI-908234`). Invoice status transitions from `UNPAID` to `PAID`.

### Scenario D: Late Renewal with Penalty Fee Calculation

A member renews an expired membership beyond the configured 5-day grace period. In accordance with Business Rule 4, the system automatically appends the plan's late penalty fee (₹100.00) onto the generated invoice alongside standard taxes.

### Scenario E: Business Rule 1 Inactive Subscription Validation

Attempting to record attendance for `Rohan Verma` (ID: `3`) who has no active subscription. The system catches `InactiveSubscriptionException`, prints `\[!] Attendance Denied: Member has no active subscription.`, and refuses to persist an attendance check-in.




