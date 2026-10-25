package com.fooddelivery.repository;

import com.fooddelivery.model.FoodItem;
import com.fooddelivery.model.RestaurantOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    
    // Abstracted query method provided by Spring Data JPA
    List<FoodItem> findByOwner(RestaurantOwner owner);
}
