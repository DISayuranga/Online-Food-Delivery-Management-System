package com.fooddelivery.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// OOP: Inheritance
@Entity
@DiscriminatorValue("BEVERAGE")
public class Beverage extends FoodItem {

    // OOP: Polymorphism - Beverages have a 5% tax and a $1 cup fee
    @Override
    public double calculatePrice() {
        return getBasePrice() + (getBasePrice() * 0.05) + 1.0;
    }

    @Override
    public String getItemType() {
        return "Beverage";
    }
}
