package com.fooddelivery.repository;

import com.fooddelivery.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCustomer(Customer customer);

    @Query("SELECT r FROM FoodReview r WHERE r.foodItem = :foodItem")
    List<FoodReview> findByFoodItem(@Param("foodItem") FoodItem foodItem);

    @Query("SELECT r FROM DeliveryReview r WHERE r.rider = :rider")
    List<DeliveryReview> findByRider(@Param("rider") Rider rider);
}
