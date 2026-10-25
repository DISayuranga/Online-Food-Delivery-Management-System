package com.fooddelivery.controller;

import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.model.RestaurantOwner;
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

    @GetMapping
    public ResponseEntity<?> index() {
        List<RestaurantOwner> restaurants = userRepository.findAllRestaurantOwners();
        if (restaurants.size() > 3) {
            restaurants = restaurants.subList(0, 3);
        }
        
        List<Map<String, Object>> result = restaurants.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("restaurantName", r.getRestaurantName());
            map.put("fullName", r.getFullName());
            return map;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(Map.of("featuredRestaurants", result));
    }
}
