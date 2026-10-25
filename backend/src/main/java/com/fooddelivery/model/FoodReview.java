package com.fooddelivery.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import com.fasterxml.jackson.annotation.JsonIgnore;

// OOP: Inheritance
@Entity
@DiscriminatorValue("FOOD")
public class FoodReview extends Review {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id")
    private FoodItem foodItem;

    // OOP: Polymorphism
    @Override
    public String getReviewCategory() {
        return "Food Quality";
    }

    @Override
    public String getReviewTargetName() {
        return foodItem != null ? foodItem.getName() : "Unknown Food Item";
    }

    @Override
    public String getReviewType() {
        return "FOOD";
    }

    public FoodItem getFoodItem() { return foodItem; }
    public void setFoodItem(FoodItem foodItem) { this.foodItem = foodItem; }
}
