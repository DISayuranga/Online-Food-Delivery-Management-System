package com.fooddelivery.repository;

import com.fooddelivery.model.CartItem;
import com.fooddelivery.model.Customer;
import com.fooddelivery.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCustomer(Customer customer);
    List<CartItem> findByFoodItem(FoodItem foodItem);
    Optional<CartItem> findByCustomerAndFoodItem(Customer customer, FoodItem foodItem);
}
