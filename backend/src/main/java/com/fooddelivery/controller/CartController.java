package com.fooddelivery.controller;

import com.fooddelivery.model.CartItem;
import com.fooddelivery.model.Customer;
import com.fooddelivery.model.FoodItem;
import com.fooddelivery.model.User;
import com.fooddelivery.repository.CartItemRepository;
import com.fooddelivery.repository.FoodItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private com.fooddelivery.repository.UserRepository userRepository;

    private Customer getCustomerByUsername(String username) {
        if (username == null || username.isEmpty()) return null;
        User user = userRepository.findByUsername(username).orElse(null);
        if (user instanceof Customer) {
            return (Customer) user;
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<?> viewCart(@RequestParam String username) {
        Customer customer = getCustomerByUsername(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);
        double totalValue = cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum();

        List<Map<String, Object>> formattedItems = cartItems.stream().map(ci -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", ci.getId());
            map.put("quantity", ci.getQuantity());
            map.put("totalPrice", ci.getTotalPrice());

            Map<String, Object> foodMap = new HashMap<>();
            FoodItem fi = ci.getFoodItem();
            if (fi != null) {
                foodMap.put("id", fi.getId());
                foodMap.put("name", fi.getName());
                foodMap.put("description", fi.getDescription());
                foodMap.put("basePrice", fi.getBasePrice());
                foodMap.put("finalPrice", fi.getFinalPrice());
                foodMap.put("imageUrl", fi.getImageUrl());
                foodMap.put("itemType", fi.getItemType());
            }
            map.put("foodItem", foodMap);
            return map;
        }).collect(java.util.stream.Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("cartItems", formattedItems);
        response.put("totalValue", totalValue);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestParam String username, @RequestParam Long foodItemId, @RequestParam int quantity) {
        Customer customer = getCustomerByUsername(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        FoodItem product = foodItemRepository.findById(foodItemId).orElse(null);
        if (product == null || quantity <= 0) return ResponseEntity.badRequest().body(Map.of("error", "Invalid product or quantity"));

        Optional<CartItem> existingItem = cartItemRepository.findByCustomerAndFoodItem(customer, product);
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCustomer(customer);
            newItem.setFoodItem(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long id, @RequestParam String username, @RequestParam int quantity) {
        Customer customer = getCustomerByUsername(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        CartItem item = cartItemRepository.findById(id).orElse(null);
        if (item != null && item.getCustomer().getId().equals(customer.getId())) {
            if (quantity <= 0) {
                cartItemRepository.delete(item);
            } else {
                item.setQuantity(quantity);
                cartItemRepository.save(item);
            }
        }
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/remove/{id}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long id, @RequestParam String username) {
        Customer customer = getCustomerByUsername(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        CartItem item = cartItemRepository.findById(id).orElse(null);
        if (item != null && item.getCustomer().getId().equals(customer.getId())) {
            cartItemRepository.delete(item);
        }
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/clear")
    public ResponseEntity<?> clearCart(@RequestParam String username) {
        Customer customer = getCustomerByUsername(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);
        cartItemRepository.deleteAll(cartItems);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
