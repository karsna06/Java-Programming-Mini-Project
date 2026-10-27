package com.gym;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a single attendance record for a gym member.
 */
public class AttendanceRecord {

    private static int idCounter = 1;

    public enum AttendanceType {
        CHECK_IN, CHECK_OUT
    }

    private final int recordId;
    private final Member member;
    private final LocalDate date;
    private final LocalTime timeIn;
    private LocalTime timeOut;
    private final String notes;

    public AttendanceRecord(Member member, String notes) {
        this.recordId = idCounter++;
        this.member   = member;
        this.date     = LocalDate.now();
        this.timeIn   = LocalTime.now().withSecond(0).withNano(0);
        this.notes    = notes;
    }

    // ---------- Getters ----------

    public int getRecordId()       { return recordId; }
    public Member getMember()      { return member; }
    public LocalDate getDate()     { return date; }
    public LocalTime getTimeIn()   { return timeIn; }
    public LocalTime getTimeOut()  { return timeOut; }
    public String getNotes()       { return notes; }

    public void setTimeOut(LocalTime timeOut) { this.timeOut = timeOut; }

    @Override
    public String toString() {
        String out = (timeOut != null) ? timeOut.toString() : "Still In";
        return String.format(
            "  [#%d] Date: %s | Time In: %s | Time Out: %s | Notes: %s",
            recordId, date, timeIn, out, (notes != null && !notes.isEmpty()) ? notes : "-"
        );
    }
}
