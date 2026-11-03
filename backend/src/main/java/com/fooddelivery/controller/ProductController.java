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
    public ResponseEntity<?> getProductsApi(@RequestParam(required = false) String category,
                                            @RequestParam(required = false) Long restaurantId,
                                            @RequestParam(required = false) String ownerUsername) {
        List<FoodItem> items = foodItemRepository.findAll();
        if (ownerUsername != null && !ownerUsername.trim().isEmpty()) {
            items = items.stream()
                    .filter(item -> item.getOwner() != null && item.getOwner().getUsername().equalsIgnoreCase(ownerUsername.trim()))
                    .collect(Collectors.toList());
        }
        if (restaurantId != null) {
            items = items.stream()
                    .filter(item -> item.getOwnerId() != null && item.getOwnerId().equals(restaurantId))
                    .collect(Collectors.toList());
        }
        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL")) {
            String catFilter = category.trim().toLowerCase();
            items = items.stream()
                    .filter(item -> (item.getCategory() != null && item.getCategory().toLowerCase().contains(catFilter))
                                 || (item.getItemType() != null && item.getItemType().toLowerCase().contains(catFilter)))
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(items);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyProducts(@RequestParam String username) {
        RestaurantOwner owner = getOwner(username);
        if (owner == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        return ResponseEntity.ok(foodItemRepository.findByOwner(owner));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestParam String username,
                                        @RequestParam String name,
                                        @RequestParam String description,
                                        @RequestParam double basePrice,
                                        @RequestParam String type,
                                        @RequestParam(required = false) String category,
                                        @RequestParam(required = false) String imageUrl,
                                        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                        @RequestParam(defaultValue = "true") boolean available) {
        RestaurantOwner owner = getOwner(username);
        if (owner == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        // Process device image upload if provided
        String finalImageUrl = imageUrl;
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String originalFilename = imageFile.getOriginalFilename();
                String ext = ".jpg";
                if (originalFilename != null && originalFilename.lastIndexOf(".") > 0) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String filename = "dish_" + System.currentTimeMillis() + ext;
                byte[] bytes = imageFile.getBytes();

                File[] targetDirs = new File[] {
                    new File("uploads"),
                    new File("frontend/uploads").exists() ? new File("frontend/uploads") : new File("../frontend/uploads"),
                    new File("target/classes/static/uploads")
                };
                for (File dir : targetDirs) {
                    if (!dir.exists()) dir.mkdirs();
                    File dest = new File(dir, filename);
                    Files.write(dest.toPath(), bytes);
                }
                finalImageUrl = "/uploads/" + filename;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // OOP: Polymorphism - Instantiating specific subclasses
        FoodItem newItem;
        switch (type.toUpperCase()) {
            case "MAIN_COURSE":
                newItem = new MainCourse();
                break;
            case "DESSERT":
                newItem = new Dessert();
                break;
            case "BEVERAGE":
                newItem = new Beverage();
                break;
            default:
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid food type"));
        }

        newItem.setName(name);
        newItem.setDescription(description);
        newItem.setBasePrice(basePrice);
        newItem.setImageUrl(finalImageUrl);
        newItem.setAvailable(available);
        newItem.setOwner(owner);
        if (category != null && !category.trim().isEmpty()) {
            newItem.setCategory(category.trim());
        } else {
            newItem.setCategory(newItem.getItemType());
        }

        foodItemRepository.save(newItem);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id, @RequestParam String username) {
        RestaurantOwner owner = getOwner(username);
        if (owner == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        FoodItem product = foodItemRepository.findById(id).orElse(null);
        if (product == null || !product.getOwner().getId().equals(owner.getId())) {
            return ResponseEntity.status(404).body(Map.of("error", "Product not found or unauthorized"));
        }
        return ResponseEntity.ok(product);
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<?> editProduct(@PathVariable Long id,
                                         @RequestParam String username,
                                         @RequestParam String name,
                                         @RequestParam String description,
                                         @RequestParam double basePrice,
                                         @RequestParam(required = false) String category,
                                         @RequestParam(required = false) String imageUrl,
                                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                         @RequestParam(defaultValue = "true") boolean available) {
        RestaurantOwner owner = getOwner(username);
        if (owner == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        FoodItem product = foodItemRepository.findById(id).orElse(null);
        if (product == null || !product.getOwner().getId().equals(owner.getId())) {
            return ResponseEntity.status(404).body(Map.of("error", "Product not found or unauthorized"));
        }

        // Process device image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String originalFilename = imageFile.getOriginalFilename();
                String ext = ".jpg";
                if (originalFilename != null && originalFilename.lastIndexOf(".") > 0) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String filename = "dish_" + System.currentTimeMillis() + ext;
                byte[] bytes = imageFile.getBytes();

                File[] targetDirs = new File[] {
                    new File("uploads"),
                    new File("frontend/uploads").exists() ? new File("frontend/uploads") : new File("../frontend/uploads"),
                    new File("target/classes/static/uploads")
                };
                for (File dir : targetDirs) {
                    if (!dir.exists()) dir.mkdirs();
                    File dest = new File(dir, filename);
                    Files.write(dest.toPath(), bytes);
                }
                product.setImageUrl("/uploads/" + filename);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (imageUrl != null && !imageUrl.isEmpty()) {
            product.setImageUrl(imageUrl);
        }

        product.setName(name);
        product.setDescription(description);
        product.setBasePrice(basePrice);
        product.setAvailable(available);
        if (category != null && !category.trim().isEmpty()) {
            product.setCategory(category.trim());
        }
        foodItemRepository.save(product);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, @RequestParam String username) {
        RestaurantOwner owner = getOwner(username);
        if (owner == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        FoodItem product = foodItemRepository.findById(id).orElse(null);
        if (product == null || !product.getOwner().getId().equals(owner.getId())) {
            return ResponseEntity.status(404).body(Map.of("error", "Product not found or unauthorized"));
        }

        // Clean up references before deleting to avoid foreign key constraint failure
        cartItemRepository.deleteAll(cartItemRepository.findByFoodItem(product));
        reviewRepository.deleteAll(reviewRepository.findByFoodItem(product));

        foodItemRepository.delete(product);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
