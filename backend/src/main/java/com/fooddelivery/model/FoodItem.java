package com.fooddelivery.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// OOP: Abstraction - Base class for different types of food items
@Entity
@Table(name = "food_items")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "item_type", discriminatorType = DiscriminatorType.STRING)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class FoodItem {

    // OOP: Encapsulation & Information Hiding
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private double basePrice;

    @Column(length = 1000)
    private String imageUrl;

    @Column(nullable = false)
    private boolean available = true;

    @Column(name = "category")
    private String category;

    // A food item belongs to a specific restaurant owner
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private RestaurantOwner owner;

    // OOP: Polymorphism - Abstract method to be overridden to calculate price dynamically
    public abstract double calculatePrice();

    // OOP: Polymorphism in action - Serialized getter for JSON so the calculated price reaches the frontend UI
    public double getFinalPrice() {
        return calculatePrice();
    }

    public abstract String getItemType();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public RestaurantOwner getOwner() {
        return owner;
    }

    public void setOwner(RestaurantOwner owner) {
        this.owner = owner;
    }

    public String getCategory() {
        if (category != null && !category.trim().isEmpty()) {
            return category;
        }
        String type = getItemType();
        if ("Dessert".equalsIgnoreCase(type)) return "Desserts";
        if ("Beverage".equalsIgnoreCase(type)) return "Beverages";
        return "Main Course";
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getOwnerId() {
        return owner != null ? owner.getId() : null;
    }

    public String getRestaurantName() {
        if (owner != null) {
            if (owner.getRestaurantName() != null && !owner.getRestaurantName().isBlank()) {
                return owner.getRestaurantName();
            }
            if (owner.getFullName() != null && !owner.getFullName().isBlank()) {
                return owner.getFullName();
            }
        }
        return "CraveBite Kitchen";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodItem foodItem = (FoodItem) o;
        return id != null && id.equals(foodItem.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
