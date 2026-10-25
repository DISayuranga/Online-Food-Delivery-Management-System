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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;

@CrossOrigin(origins = "*")
@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String showLoginForm() {
        return "redirect:/login.html";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            User user = userOptional.get();
            // Store the logged-in user in the session
            session.setAttribute("loggedInUser", user);
            return "redirect:/profile.html";
        }

        model.addAttribute("error", "Invalid username or password");
        return "redirect:/login.html?error=1";
    }

    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<?> loginUserApi(@RequestParam String username,
                                          @RequestParam String password,
                                          HttpSession session) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            User user = userOptional.get();
            session.setAttribute("loggedInUser", user);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("role", user.getClass().getSimpleName());
            response.put("roleType", user.getRoleType());
            response.put("username", user.getUsername());
            response.put("fullName", user.getFullName());
            return ResponseEntity.ok(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "Invalid username or password");
        return ResponseEntity.status(401).body(response);
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "redirect:/register.html";
    }

    @PostMapping("/api/register")
    @ResponseBody
    public ResponseEntity<?> registerUserApi(@RequestParam String username,
                                             @RequestParam String password,
                                             @RequestParam String fullName,
                                             @RequestParam String email,
                                             @RequestParam(required = false, defaultValue = "CUSTOMER") String role,
                                             @RequestParam(required = false) String specificField,
                                             @RequestParam(required = false) String phoneNumber) {
        
        Map<String, Object> response = new HashMap<>();
        if (userRepository.findByUsername(username).isPresent()) {
            response.put("success", false);
            response.put("error", "Username already exists.");
            return ResponseEntity.status(400).body(response);
        }

        // Public registration is strictly for Customers only.
        // Admins can create Riders, Restaurant Owners, and other Admins from the Admin Panel.
        Customer customer = new Customer();
        customer.setDefaultDeliveryAddress(specificField != null ? specificField : "");
        customer.setPhoneNumber(phoneNumber != null ? phoneNumber : "");
        customer.setUsername(username);
        customer.setPassword(password);
        customer.setFullName(fullName);
        customer.setEmail(email);

        userRepository.save(customer);
        
        response.put("success", true);
        response.put("message", "Customer account registered successfully!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login.html";
    }
}
