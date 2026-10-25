package com.fooddelivery.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// OOP: Inheritance - Customer class extends the base User class.
@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {

    // OOP: Encapsulation & Information Hiding
    private String defaultDeliveryAddress;
    private String phoneNumber;

    // OOP: Polymorphism - Overriding the getRoleName method from the parent User class.
    @Override
    public String getRoleName() {
        return "Customer";
    }

    @Override
    public String getRoleType() {
        return "CUSTOMER";
    }

    public String getDefaultDeliveryAddress() {
        return defaultDeliveryAddress;
    }

    public void setDefaultDeliveryAddress(String defaultDeliveryAddress) {
        this.defaultDeliveryAddress = defaultDeliveryAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
