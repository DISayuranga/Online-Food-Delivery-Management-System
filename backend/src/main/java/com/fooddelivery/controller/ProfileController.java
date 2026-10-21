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
