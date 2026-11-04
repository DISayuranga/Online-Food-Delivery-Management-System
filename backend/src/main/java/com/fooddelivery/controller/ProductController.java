package com.fooddelivery.controller;

import com.fooddelivery.model.*;
import com.fooddelivery.repository.FoodItemRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.fooddelivery.repository.CartItemRepository cartItemRepository;

    @Autowired
    private com.fooddelivery.repository.ReviewRepository reviewRepository;

    private RestaurantOwner getOwner(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        return (user instanceof RestaurantOwner) ? (RestaurantOwner) user : null;
    }

    @GetMapping("/list")
    public ResponseEntity<?> getProductsApi() {
        return ResponseEntity.ok(foodItemRepository.findAll());
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyProducts(@RequestParam String username) {
        RestaurantOwner owner = getOwner(username);
        if (owner == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        return ResponseEntity.ok(foodItemRepository.findByOwner(owner));
    }

   
}
