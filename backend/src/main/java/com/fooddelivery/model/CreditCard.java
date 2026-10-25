package com.fooddelivery.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// OOP: Inheritance
@Entity
@DiscriminatorValue("CREDIT")
public class CreditCard extends PaymentMethod {

    private double creditLimit;

    // OOP: Polymorphism - Credit cards might authorize against a limit
    @Override
    public String processPayment(double amount) {
        return "Processed credit card payment of Rs. " + String.format("%.2f", amount) + " via Visa/Mastercard network.";
    }

    @Override
    public String getCardType() {
        return "CREDIT";
    }

    @Override
    public String getCardSpecificDetail() {
        return "Credit Limit: Rs. " + String.format("%.2f", creditLimit);
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }
}
