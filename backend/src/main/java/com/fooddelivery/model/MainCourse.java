package com.fooddelivery.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// OOP: Inheritance
@Entity
@DiscriminatorValue("MAIN_COURSE")
public class MainCourse extends FoodItem {

    // OOP: Polymorphism - Main course adds a standard 10% preparation tax
    @Override
    public double calculatePrice() {
        return getBasePrice() + (getBasePrice() * 0.10);
    }

    @Override
    public String getItemType() {
        return "Main Course";
    }
}
