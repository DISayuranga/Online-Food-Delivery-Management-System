package com.fooddelivery.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// OOP: Inheritance - RestaurantOwner extends the User base class.
@Entity
@DiscriminatorValue("RESTAURANT_OWNER")
public class RestaurantOwner extends User {

    // OOP: Encapsulation
    private String restaurantName;

    // OOP: Polymorphism - Overriding the abstract behavior.
    @Override
    public String getRoleName() {
        return "Restaurant Owner";
    }

    @Override
    public String getRoleType() {
        return "RESTAURANT_OWNER";
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}
