package com.fooddelivery.controller;

import com.fooddelivery.model.*;
import com.fooddelivery.repository.ContactMessageRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/contact")
public class ContactController {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/targets")
    public ResponseEntity<?> getContactTargets() {
        List<Map<String, Object>> targets = new ArrayList<>();

        // 1. General Admin Support
        Map<String, Object> adminTarget = new HashMap<>();
        adminTarget.put("targetType", "ADMIN");
        adminTarget.put("targetId", null);
        adminTarget.put("displayName", "🛡️ Platform Support & Admin Team");
        targets.add(adminTarget);

        // 2. All Registered Restaurants
        List<User> allUsers = userRepository.findAll();
        for (User u : allUsers) {
            if (u instanceof RestaurantOwner) {
                RestaurantOwner owner = (RestaurantOwner) u;
                Map<String, Object> rMap = new HashMap<>();
                rMap.put("targetType", "RESTAURANT");
                rMap.put("targetId", owner.getId());
                String rName = owner.getRestaurantName() != null && !owner.getRestaurantName().isEmpty() 
                        ? owner.getRestaurantName() : owner.getFullName();
                rMap.put("displayName", "🏪 Restaurant: " + rName + " (" + owner.getFullName() + ")");
                targets.add(rMap);
            }
        }

        // 3. All Active Delivery Riders
        for (User u : allUsers) {
            if (u instanceof Rider) {
                Rider rider = (Rider) u;
                Map<String, Object> rdMap = new HashMap<>();
                rdMap.put("targetType", "RIDER");
                rdMap.put("targetId", rider.getId());
                rdMap.put("displayName", "🛵 Delivery Rider: " + rider.getFullName() + " (@" + rider.getUsername() + ")");
                targets.add(rdMap);
            }
        }

        return ResponseEntity.ok(targets);
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestParam(required = false) String customerUsername,
                                        @RequestParam String name,
                                        @RequestParam String email,
                                        @RequestParam(required = false) String phone,
                                        @RequestParam String targetType,
                                        @RequestParam(required = false) Long targetId,
                                        @RequestParam String targetName,
                                        @RequestParam String subject,
                                        @RequestParam String message) {
        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty() || message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name, email, and message are required."));
        }

        ContactMessage msg = new ContactMessage();
        msg.setCustomerUsername(customerUsername != null && !customerUsername.trim().isEmpty() ? customerUsername.trim() : "Guest");
        msg.setCustomerName(name.trim());
        msg.setCustomerEmail(email.trim());
        msg.setCustomerPhone(phone != null ? phone.trim() : "");
        msg.setTargetType(targetType != null ? targetType.toUpperCase() : "ADMIN");
        msg.setTargetId(targetId);
        msg.setTargetName(targetName != null ? targetName.trim() : "Platform Support");
        msg.setSubject(subject != null && !subject.trim().isEmpty() ? subject.trim() : "General Inquiry");
        msg.setMessage(message.trim());
        msg.setCreatedAt(LocalDateTime.now());
        msg.setStatus("PENDING");

        contactMessageRepository.save(msg);
        return ResponseEntity.ok(Map.of("success", true, "message", "Your message has been sent successfully."));
    }

    @GetMapping("/my-messages")
    public ResponseEntity<?> getCustomerMessages(@RequestParam String username) {
        List<ContactMessage> list = contactMessageRepository.findByCustomerUsernameOrderByCreatedAtDesc(username);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/inbox")
    public ResponseEntity<?> getInboxForRole(@RequestParam String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        if (user instanceof Admin) {
            // Admins can see all messages
            List<ContactMessage> all = contactMessageRepository.findAllByOrderByCreatedAtDesc();
            return ResponseEntity.ok(Map.of("messages", all, "role", "ADMIN"));
        } else if (user instanceof RestaurantOwner) {
            // Restaurant sees messages addressed to their restaurant
            RestaurantOwner owner = (RestaurantOwner) user;
            List<ContactMessage> list = contactMessageRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc("RESTAURANT", owner.getId());
            return ResponseEntity.ok(Map.of("messages", list, "role", "RESTAURANT"));
        } else if (user instanceof Rider) {
            // Rider sees messages addressed to them
            Rider rider = (Rider) user;
            List<ContactMessage> list = contactMessageRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc("RIDER", rider.getId());
            return ResponseEntity.ok(Map.of("messages", list, "role", "RIDER"));
        }

        return ResponseEntity.status(403).body(Map.of("error", "Unauthorized"));
    }

    @PostMapping("/reply/{id}")
    public ResponseEntity<?> replyMessage(@PathVariable Long id,
                                         @RequestParam String username,
                                         @RequestParam String replyMessage) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        ContactMessage msg = contactMessageRepository.findById(id).orElse(null);
        if (msg == null) return ResponseEntity.badRequest().body(Map.of("error", "Message not found"));

        if (replyMessage == null || replyMessage.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Reply text cannot be empty"));
        }

        String role = "ADMIN";
        String responderName = user.getFullName();

        if (user instanceof Admin) {
            role = "ADMIN";
            responderName = user.getFullName() + " (Platform Support & Admin)";
        } else if (user instanceof RestaurantOwner) {
            RestaurantOwner owner = (RestaurantOwner) user;
            role = "RESTAURANT";
            String rName = owner.getRestaurantName() != null ? owner.getRestaurantName() : owner.getFullName();
            responderName = rName + " (Restaurant Owner - " + owner.getFullName() + ")";
        } else if (user instanceof Rider) {
            role = "RIDER";
            responderName = user.getFullName() + " (Assigned Rider)";
        } else {
            return ResponseEntity.status(403).body(Map.of("error", "Only Admin, Restaurant Owners, or Riders can reply"));
        }

        msg.setReply(replyMessage.trim());
        msg.setRepliedByRole(role);
        msg.setRepliedByName(responderName);
        msg.setRepliedAt(LocalDateTime.now());
        msg.setStatus("REPLIED");

        contactMessageRepository.save(msg);
        return ResponseEntity.ok(Map.of("success", true, "message", "Reply posted successfully!", "item", msg));
    }
}
