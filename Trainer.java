package com.gym;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a gym trainer who can be assigned to members.
 */
public class Trainer {

    private static int idCounter = 1;

    public enum Specialization {
        WEIGHT_TRAINING, CARDIO, YOGA, ZUMBA, CROSSFIT, SWIMMING, NUTRITION, GENERAL
    }

    private final int trainerId;
    private String name;
    private String email;
    private String phone;
    private Specialization specialization;
    private double monthlyFee;
    private final List<Member> assignedMembers;

    public Trainer(String name, String email, String phone,
                   Specialization specialization, double monthlyFee) {
        this.trainerId        = idCounter++;
        this.name             = name;
        this.email            = email;
        this.phone            = phone;
        this.specialization   = specialization;
        this.monthlyFee       = monthlyFee;
        this.assignedMembers  = new ArrayList<>();
    }

    // ---------- Getters & Setters ----------

    public int getTrainerId()                    { return trainerId; }
    public String getName()                      { return name; }
    public void setName(String n)                { this.name = n; }
    public String getEmail()                     { return email; }
    public void setEmail(String e)               { this.email = e; }
    public String getPhone()                     { return phone; }
    public void setPhone(String p)               { this.phone = p; }
    public Specialization getSpecialization()    { return specialization; }
    public void setSpecialization(Specialization s){ this.specialization = s; }
    public double getMonthlyFee()                { return monthlyFee; }
    public void setMonthlyFee(double f)          { this.monthlyFee = f; }
    public List<Member> getAssignedMembers()     { return assignedMembers; }

    public void assignMember(Member m) {
        if (!assignedMembers.contains(m)) {
            assignedMembers.add(m);
            m.setAssignedTrainer(this);
        }
    }

    public void removeMember(Member m) {
        assignedMembers.remove(m);
    }

    @Override
    public String toString() {
        return String.format(
            "  Trainer #%d | Name: %-20s | Specialization: %-15s | Fee: Rs.%.2f/month | Members: %d",
            trainerId, name, specialization, monthlyFee, assignedMembers.size()
        );
    }
}
