package com.fooddelivery.controller;

import com.fooddelivery.model.Customer;
import com.fooddelivery.model.RestaurantOwner;
import com.fooddelivery.model.Rider;
import com.fooddelivery.model.User;
import com.fooddelivery.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> showProfile(@RequestParam String username) {
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not found"));
        }
        
        return ResponseEntity.ok(Map.of("user", user, "roleName", user.getRoleName()));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestParam String username,
                                           @RequestParam String fullName,
                                           @RequestParam String email,
                                           @RequestParam(required = false) String specificField,
                                           @RequestParam(required = false) String phoneNumber) {
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        
        User userToUpdate = userRepository.findByUsername(username).orElse(null);
        if (userToUpdate != null) {
            userToUpdate.setFullName(fullName);
            userToUpdate.setEmail(email);
            
            if (userToUpdate instanceof Customer) {
                ((Customer) userToUpdate).setDefaultDeliveryAddress(specificField);
                ((Customer) userToUpdate).setPhoneNumber(phoneNumber);
            } else if (userToUpdate instanceof Rider) {
                ((Rider) userToUpdate).setVehiclePlateNumber(specificField);
                ((Rider) userToUpdate).setPhoneNumber(phoneNumber);
            } else if (userToUpdate instanceof RestaurantOwner) {
                ((RestaurantOwner) userToUpdate).setRestaurantName(specificField);
            }
            
            userRepository.save(userToUpdate);
            return ResponseEntity.ok(Map.of("success", true));
        }
        
        return ResponseEntity.status(404).body(Map.of("error", "User not found"));
    }

    @Autowired
    private com.fooddelivery.service.UserService userService;

    @PostMapping("/picture/upload")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam String username,
                                                  @RequestParam("profileImage") org.springframework.web.multipart.MultipartFile profileImage) {
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(404).body(Map.of("error", "User not found"));

        if (profileImage == null || profileImage.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No image provided"));
        }

        try {
            String originalFilename = profileImage.getOriginalFilename();
            String ext = ".jpg";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = "profile_" + user.getId() + "_" + System.currentTimeMillis() + ext;

            java.io.File uploadDir1 = new java.io.File("uploads");
            if (!uploadDir1.exists()) uploadDir1.mkdirs();
            java.nio.file.Path target1 = uploadDir1.toPath().resolve(filename);
            java.nio.file.Files.write(target1, profileImage.getBytes());

            java.io.File uploadDir2 = new java.io.File("frontend/uploads").exists() ? new java.io.File("frontend/uploads") : new java.io.File("../frontend/uploads");
            if (!uploadDir2.exists()) uploadDir2.mkdirs();
            java.nio.file.Path target2 = uploadDir2.toPath().resolve(filename);
            java.nio.file.Files.write(target2, profileImage.getBytes());

            user.setProfilePictureUrl("/uploads/" + filename);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("success", true, "message", "Profile picture updated successfully!", "profilePictureUrl", "/uploads/" + filename));
        } catch (java.io.IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload profile picture: " + e.getMessage()));
        }
    }

    @PostMapping("/picture/delete")
    public ResponseEntity<?> deleteProfilePicture(@RequestParam String username) {
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(404).body(Map.of("error", "User not found"));

        user.setProfilePictureUrl(null);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("success", true, "message", "Profile picture removed successfully!"));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteProfile(@RequestParam String username) {
        if (username == null || username.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        
        User userToDelete = userRepository.findByUsername(username).orElse(null);
        if (userToDelete != null) {
            userService.deleteUserCascaded(userToDelete.getId());
            return ResponseEntity.ok(Map.of("success", true));
        }
        
        return ResponseEntity.status(404).body(Map.of("error", "User not found"));
    }
}
