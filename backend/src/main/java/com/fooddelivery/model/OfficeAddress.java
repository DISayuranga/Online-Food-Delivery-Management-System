package com.fooddelivery.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// OOP: Inheritance
@Entity
@DiscriminatorValue("OFFICE")
public class OfficeAddress extends Address {

    private String floorAndCompany;

    // OOP: Polymorphism
    @Override
    public String getDeliveryInstructions() {
        return "Leave at reception. Details: " + (floorAndCompany != null ? floorAndCompany : "N/A");
    }

    @Override
    public String getAddressType() {
        return "OFFICE";
    }

    @Override
    public String getSpecificDetail() {
        return floorAndCompany != null ? floorAndCompany : "";
    }

    public String getFloorAndCompany() { return floorAndCompany; }
    public void setFloorAndCompany(String floorAndCompany) { this.floorAndCompany = floorAndCompany; }
}
