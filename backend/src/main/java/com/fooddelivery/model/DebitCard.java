package com.fooddelivery.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// OOP: Inheritance
@Entity
@DiscriminatorValue("DEBIT")
public class DebitCard extends PaymentMethod {

    private String bankName;

    // OOP: Polymorphism - Debit cards check direct bank balance
    @Override
    public String processPayment(double amount) {
        return "Processed debit card payment of Rs. " + String.format("%.2f", amount) + " via direct bank transfer (" + bankName + ").";
    }

    @Override
    public String getCardType() {
        return "DEBIT";
    }

    @Override
    public String getCardSpecificDetail() {
        return "Bank: " + (bankName != null ? bankName : "N/A");
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
