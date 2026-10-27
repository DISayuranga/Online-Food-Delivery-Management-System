package com.fooddelivery.controller;

import com.fooddelivery.model.FoodItem;
import com.fooddelivery.model.RestaurantOwner;
import com.fooddelivery.repository.FoodItemRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @GetMapping
    public ResponseEntity<?> index() {
        List<RestaurantOwner> restaurants = userRepository.findAllRestaurantOwners();
        
        List<Map<String, Object>> result = restaurants.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            String rName = (r.getRestaurantName() != null && !r.getRestaurantName().isBlank()) 
                    ? r.getRestaurantName() 
                    : r.getFullName();
            map.put("restaurantName", rName);
            map.put("fullName", r.getFullName());
            map.put("username", r.getUsername());
            map.put("email", r.getEmail());

            List<FoodItem> items = foodItemRepository.findByOwner(r);
            map.put("itemCount", items.size());

            // Get top dish names
            List<String> sampleNames = items.stream()
                    .map(FoodItem::getName)
                    .limit(3)
                    .collect(Collectors.toList());
            map.put("sampleDishes", sampleNames);

            // Get a preview image from food items if available
            String previewImage = items.stream()
                    .map(FoodItem::getImageUrl)
                    .filter(img -> img != null && !img.isBlank())
                    .findFirst()
                    .orElse("https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=600&q=80");
            map.put("previewImage", previewImage);
            map.put("rating", "4.8");
            map.put("deliveryTime", "20-30 min");

            return map;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(Map.of("featuredRestaurants", result));
    }
}
