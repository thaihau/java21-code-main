package com.course.lab02.modern;

import java.util.Objects;

/**
 * The "Legacy" Data Model.
 * PROBLEM: Boilerplate overload.
 * To make this a proper data carrier, we had to write 50+ lines of code.
 * Also, the JSON generation in toReport() is ugly and error-prone.
 */
public class LegacyTransaction {

    private final String id;
    private final double amount;
    private final String type; // e.g., "CREDIT", "DEBIT"

    public LegacyTransaction(String id, double amount, String type) {
        this.id = id;
        this.amount = amount;
        this.type = type;
    }

    public String getId() { return id; }
    public double getAmount() { return amount; }
    public String getType() { return type; }

    // BOILERPLATE 1: equals()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegacyTransaction that = (LegacyTransaction) o;
        return Double.compare(that.amount, amount) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(type, that.type);
    }

    // BOILERPLATE 2: hashCode()
    @Override
    public int hashCode() {
        return Objects.hash(id, amount, type);
    }

    // BOILERPLATE 3: toString()
    @Override
    public String toString() {
        return "LegacyTransaction{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                '}';
    }

    // PROBLEM: Ugly String Concatenation (Pre-Java 15)
    public String toJSON() {
        return "{\n" +
               "  \"id\": \"" + id + "\",\n" +
               "  \"amount\": " + amount + ",\n" +
               "  \"type\": \"" + type + "\"\n" +
               "}";
    }
}