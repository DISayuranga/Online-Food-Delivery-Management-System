package com.fooddelivery.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// OOP: Inheritance
@Entity
@DiscriminatorValue("DESSERT")
public class Dessert extends FoodItem {

    // OOP: Polymorphism - Desserts have a flat $2 sweet tax
    @Override
    public double calculatePrice() {
        return getBasePrice() + 2.0;
    }

    @Override
    public String getItemType() {
        return "Dessert";
    }
}
