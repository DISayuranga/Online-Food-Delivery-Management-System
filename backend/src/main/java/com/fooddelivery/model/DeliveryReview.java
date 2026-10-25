package com.fooddelivery.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import com.fasterxml.jackson.annotation.JsonIgnore;

// OOP: Inheritance
@Entity
@DiscriminatorValue("DELIVERY")
public class DeliveryReview extends Review {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_id")
    private Rider rider;

    // OOP: Polymorphism
    @Override
    public String getReviewCategory() {
        return "Delivery Service";
    }

    @Override
    public String getReviewTargetName() {
        return rider != null ? rider.getFullName() : "Unknown Rider";
    }

    @Override
    public String getReviewType() {
        return "DELIVERY";
    }

    public Rider getRider() { return rider; }
    public void setRider(Rider rider) { this.rider = rider; }
}
