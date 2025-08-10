package com.example.demo.Entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a single daily milk delivery record for a customer.
 * One record per customer per date.
 */
@Document(collection = "milk_entries")
@CompoundIndexes({
    @CompoundIndex(name = "customer_date_unique", def = "{'customerId': 1, 'date': 1}", unique = true)
})
public class MilkEntry {

    @Id
    private String id;

    private String customerId;     // Reference to Customer document
    private String customerName;   // Stored for quick reporting (denormalized)
    private double liters;         // Liters delivered
    private double pricePerLiter;  // Optional: price per liter for billing
    private double totalAmount;    // Optional: calculated total for the day
    private LocalDate date;        // Delivery date

    @CreatedDate
    private LocalDateTime createdAt; // Automatically set when first saved

    @LastModifiedDate
    private LocalDateTime updatedAt; // Automatically set when updated

    public MilkEntry() {}

    public MilkEntry(String customerId, String customerName, double liters, double pricePerLiter, LocalDate date) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.liters = liters;
        this.pricePerLiter = pricePerLiter;
        this.totalAmount = liters * pricePerLiter;
        this.date = date;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getLiters() {
        return liters;
    }

    public void setLiters(double liters) {
        this.liters = liters;
        recalculateTotal();
    }

    public double getPricePerLiter() {
        return pricePerLiter;
    }

    public void setPricePerLiter(double pricePerLiter) {
        this.pricePerLiter = pricePerLiter;
        recalculateTotal();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    private void recalculateTotal() {
        this.totalAmount = this.liters * this.pricePerLiter;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
