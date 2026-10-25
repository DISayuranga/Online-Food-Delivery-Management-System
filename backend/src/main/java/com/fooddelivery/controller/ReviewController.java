package com.fooddelivery.controller;

import com.fooddelivery.model.*;
import com.fooddelivery.repository.FoodItemRepository;
import com.fooddelivery.repository.ReviewRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;
    
    @Autowired
    private UserRepository userRepository;

    private Customer getCustomer(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        return (user instanceof Customer) ? (Customer) user : null;
    }

    @GetMapping
    public ResponseEntity<?> viewMyReviews(@RequestParam String username) {
        Customer customer = getCustomer(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        List<Review> myReviews = reviewRepository.findByCustomer(customer);
        List<Map<String, Object>> result = myReviews.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("rating", r.getRating());
            map.put("comments", r.getComments());
            map.put("reviewCategory", r.getReviewCategory());
            map.put("reviewTargetName", r.getReviewTargetName());
            return map;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/form-data")
    public ResponseEntity<?> getFormData() {
        List<FoodItem> foodItems = foodItemRepository.findAll();
        List<Map<String, Object>> items = foodItems.stream().map(fi -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", fi.getId());
            map.put("name", fi.getName());
            return map;
        }).collect(Collectors.toList());

        List<Rider> riders = userRepository.findAll().stream()
            .filter(u -> u instanceof Rider)
            .map(u -> (Rider) u)
            .collect(Collectors.toList());

        List<Map<String, Object>> riderList = riders.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("name", r.getFullName() + " (" + (r.getVehiclePlateNumber() != null ? r.getVehiclePlateNumber() : "Rider") + ")");
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("foodItems", items, "riders", riderList));
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitReview(@RequestParam String username,
                                          @RequestParam String reviewType,
                                          @RequestParam int rating,
                                          @RequestParam String comments,
                                          @RequestParam(required = false) Long targetId) {
        Customer customer = getCustomer(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body(Map.of("error", "Rating must be between 1 and 5"));
        }

        // OOP: Polymorphism - Instantiate the specific review subclass
        Review newReview;

        if ("FOOD".equals(reviewType)) {
            FoodReview foodReview = new FoodReview();
            if (targetId != null) {
                foodItemRepository.findById(targetId).ifPresent(foodReview::setFoodItem);
            }
            newReview = foodReview;
        } else if ("DELIVERY".equals(reviewType)) {
            DeliveryReview deliveryReview = new DeliveryReview();
            if (targetId != null) {
                userRepository.findById(targetId).ifPresent(user -> {
                    if (user instanceof Rider) deliveryReview.setRider((Rider) user);
                });
            }
            newReview = deliveryReview;
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid review type"));
        }

        newReview.setRating(rating);
        newReview.setComments(comments);
        newReview.setCustomer(customer);

        reviewRepository.save(newReview);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<?> getReview(@PathVariable Long id, @RequestParam String username) {
        Customer customer = getCustomer(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        Review review = reviewRepository.findById(id).orElse(null);
        if (review == null || !review.getCustomer().getId().equals(customer.getId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Not found"));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", review.getId());
        result.put("rating", review.getRating());
        result.put("comments", review.getComments());
        result.put("reviewCategory", review.getReviewCategory());
        result.put("reviewTargetName", review.getReviewTargetName());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id,
                                          @RequestParam String username,
                                          @RequestParam int rating, 
                                          @RequestParam String comments) {
        Customer customer = getCustomer(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        Review review = reviewRepository.findById(id).orElse(null);
        if (review != null && review.getCustomer().getId().equals(customer.getId())) {
            review.setRating(rating);
            review.setComments(comments);
            reviewRepository.save(review);
        }

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id, @RequestParam String username) {
        Customer customer = getCustomer(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        Review review = reviewRepository.findById(id).orElse(null);
        if (review != null && review.getCustomer().getId().equals(customer.getId())) {
            reviewRepository.delete(review);
        }

        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/restaurant")
    public ResponseEntity<?> viewRestaurantReviews(@RequestParam String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (!(user instanceof RestaurantOwner)) return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        RestaurantOwner owner = (RestaurantOwner) user;
        
        List<FoodItem> dishes = foodItemRepository.findByOwner(owner);
        List<Map<String, Object>> reviews = new ArrayList<>();
        
        for (FoodItem item : dishes) {
            List<FoodReview> itemReviews = reviewRepository.findByFoodItem(item);
            for (FoodReview r : itemReviews) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", r.getId());
                map.put("rating", r.getRating());
                map.put("comments", r.getComments());
                map.put("customerName", r.getCustomer() != null ? r.getCustomer().getFullName() : "Anonymous");
                map.put("dishName", item.getName());
                reviews.add(map);
            }
        }
        return ResponseEntity.ok(reviews);
    }
}
