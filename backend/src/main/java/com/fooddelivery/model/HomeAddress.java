package com.fooddelivery.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// OOP: Inheritance
@Entity
@DiscriminatorValue("HOME")
public class HomeAddress extends Address {

    private String gateCode;

    // OOP: Polymorphism
    @Override
    public String getDeliveryInstructions() {
        return "Leave at the front door. Gate Code (if any): " + (gateCode != null ? gateCode : "N/A");
    }

    @Override
    public String getAddressType() {
        return "HOME";
    }

    @Override
    public String getSpecificDetail() {
        return gateCode != null ? gateCode : "";
    }

    public String getGateCode() { return gateCode; }
    public void setGateCode(String gateCode) { this.gateCode = gateCode; }
}
