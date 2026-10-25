package com.fooddelivery.controller;

import com.fooddelivery.model.*;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    private boolean isAdmin(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        return user instanceof Admin;
    }

    @GetMapping
    public ResponseEntity<?> viewDashboard(@RequestParam String username, @RequestParam(required = false) String search) {
        if (!isAdmin(username)) return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));

        List<User> users;
        if (search != null && !search.trim().isEmpty()) {
            users = userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrUsernameContainingIgnoreCase(search, search, search);
        } else {
            users = userRepository.findAll();
        }

        List<Map<String, Object>> result = users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("fullName", u.getFullName());
            map.put("email", u.getEmail());
            map.put("roleName", u.getRoleName());
            if (u instanceof Customer) {
                map.put("specificField", ((Customer) u).getDefaultDeliveryAddress());
            } else if (u instanceof Rider) {
                map.put("specificField", ((Rider) u).getVehiclePlateNumber());
            } else if (u instanceof RestaurantOwner) {
                map.put("specificField", ((RestaurantOwner) u).getRestaurantName());
            }
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/users/add")
    public ResponseEntity<?> addUser(@RequestParam String username,
                                     @RequestParam String adminUsername,
                                     @RequestParam String password,
                                     @RequestParam String fullName,
                                     @RequestParam String email,
                                     @RequestParam String role,
                                     @RequestParam(required = false) String specificField) {
        if (!isAdmin(adminUsername)) return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }

        User newUser = null;
        if ("CUSTOMER".equals(role)) {
            Customer customer = new Customer();
            customer.setDefaultDeliveryAddress(specificField);
            newUser = customer;
        } else if ("RIDER".equals(role)) {
            Rider rider = new Rider();
            rider.setVehiclePlateNumber(specificField);
            newUser = rider;
        } else if ("RESTAURANT_OWNER".equals(role)) {
            RestaurantOwner owner = new RestaurantOwner();
            owner.setRestaurantName(specificField);
            newUser = owner;
        } else if ("ADMIN".equals(role)) {
            newUser = new Admin();
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role"));
        }

        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFullName(fullName);
        newUser.setEmail(email);

        userRepository.save(newUser);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @Autowired
    private com.fooddelivery.service.UserService userService;

    @PostMapping("/users/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @RequestParam String adminUsername,
                                        @RequestParam String fullName,
                                        @RequestParam String email,
                                        @RequestParam(required = false) String password,
                                        @RequestParam(required = false) String specificField) {
        if (!isAdmin(adminUsername)) return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFullName(fullName);
            user.setEmail(email);
            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(password);
            }

            if (user instanceof Customer) {
                ((Customer) user).setDefaultDeliveryAddress(specificField);
            } else if (user instanceof Rider) {
                ((Rider) user).setVehiclePlateNumber(specificField);
            } else if (user instanceof RestaurantOwner) {
                ((RestaurantOwner) user).setRestaurantName(specificField);
            }

            userRepository.save(user);
            return ResponseEntity.ok(Map.of("success", true));
        }

        return ResponseEntity.status(404).body(Map.of("error", "User not found"));
    }

    @PostMapping("/users/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestParam String adminUsername) {
        if (!isAdmin(adminUsername)) return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));

        User admin = userRepository.findByUsername(adminUsername).orElse(null);
        if (admin != null && admin.getId().equals(id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot delete yourself"));
        }

        boolean deleted = userService.deleteUserCascaded(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("success", true));
        }
        return ResponseEntity.status(404).body(Map.of("error", "User not found"));
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics(@RequestParam String username) {
        if (!isAdmin(username)) return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));

        List<User> users = userRepository.findAll();
        long customersCount = users.stream().filter(u -> u instanceof Customer).count();
        long ownersCount = users.stream().filter(u -> u instanceof RestaurantOwner).count();
        long ridersCount = users.stream().filter(u -> u instanceof Rider).count();
        long adminsCount = users.stream().filter(u -> u instanceof Admin).count();

        List<OrderRecord> orders = orderRepository.findAll();
        double totalRevenue = orders.stream()
                .filter(o -> !"CANCELLED".equals(o.getOrderStatus()))
                .mapToDouble(OrderRecord::getTotalAmount)
                .sum();

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalUsers", users.size());
        analytics.put("totalCustomers", customersCount);
        analytics.put("totalRestaurants", ownersCount);
        analytics.put("totalRiders", ridersCount);
        analytics.put("totalAdmins", adminsCount);
        analytics.put("totalOrders", orders.size());
        analytics.put("totalRevenue", totalRevenue);

        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(@RequestParam String username) {
        if (!isAdmin(username)) return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        List<OrderRecord> orders = orderRepository.findAll();
        orders.sort((a, b) -> b.getOrderDate() != null && a.getOrderDate() != null ? b.getOrderDate().compareTo(a.getOrderDate()) : 0);
        
        List<Map<String, Object>> result = orders.stream().map(o -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", o.getId());
            m.put("totalAmount", o.getTotalAmount());
            m.put("orderStatus", o.getOrderStatus());
            m.put("deliveryAddress", o.getDeliveryAddress());
            m.put("orderDate", o.getOrderDate() != null ? o.getOrderDate().toString() : "");
            m.put("itemsSummary", o.getItemsSummary());
            m.put("customerName", o.getCustomer() != null ? o.getCustomer().getFullName() : "N/A");
            m.put("customerUsername", o.getCustomer() != null ? o.getCustomer().getUsername() : "N/A");
            m.put("restaurantName", o.getRestaurant() != null ? o.getRestaurant().getRestaurantName() : "N/A");
            m.put("riderName", o.getRider() != null ? o.getRider().getFullName() : "Unassigned");
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/vehicles")
    public ResponseEntity<?> getAllRiderVehicles(@RequestParam String username) {
        if (!isAdmin(username)) return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));

        List<User> users = userRepository.findAll();
        List<Map<String, Object>> vehicles = users.stream()
                .filter(u -> u instanceof Rider)
                .map(u -> {
                    Rider r = (Rider) u;
                    Map<String, Object> map = new HashMap<>();
                    map.put("riderId", r.getId());
                    map.put("username", r.getUsername());
                    map.put("fullName", r.getFullName());
                    map.put("email", r.getEmail());
                    map.put("phoneNumber", r.getPhoneNumber() != null ? r.getPhoneNumber() : "N/A");
                    map.put("vehicleType", r.getVehicleType() != null ? r.getVehicleType() : "BIKE");
                    map.put("vehicleTypeDisplay", r.getVehicleTypeDisplay());
                    map.put("vehiclePlateNumber", r.getVehiclePlateNumber() != null ? r.getVehiclePlateNumber() : "Not Registered");
                    map.put("vehicleModel", r.getVehicleModel() != null ? r.getVehicleModel() : "N/A");
                    map.put("vehicleColor", r.getVehicleColor() != null ? r.getVehicleColor() : "N/A");
                    map.put("vehicleImageUrl", r.getVehicleImageUrl() != null ? r.getVehicleImageUrl() : "");
                    String status = r.getVehicleApprovalStatus();
                    if (status == null || status.trim().isEmpty()) {
                        status = (r.getVehiclePlateNumber() != null && !r.getVehiclePlateNumber().isEmpty()) ? "APPROVED" : "PENDING_APPROVAL";
                    }
                    map.put("vehicleApprovalStatus", status);
                    map.put("vehicleAdminNotes", r.getVehicleAdminNotes() != null ? r.getVehicleAdminNotes() : "");
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(vehicles);
    }

    @PostMapping("/vehicles/validate")
    public ResponseEntity<?> validateVehicle(@RequestParam String username,
                                            @RequestParam Long riderId,
                                            @RequestParam String status,
                                            @RequestParam(required = false) String adminNotes) {
        if (!isAdmin(username)) return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));

        User user = userRepository.findById(riderId).orElse(null);
        if (!(user instanceof Rider)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Rider not found"));
        }

        Rider rider = (Rider) user;
        String normalizedStatus = "APPROVED".equalsIgnoreCase(status) ? "APPROVED" : "REJECTED";
        rider.setVehicleApprovalStatus(normalizedStatus);
        if (adminNotes != null && !adminNotes.trim().isEmpty()) {
            rider.setVehicleAdminNotes(adminNotes.trim());
        } else {
            rider.setVehicleAdminNotes("APPROVED".equals(normalizedStatus) ? "Vehicle verified by Admin" : "Vehicle rejected by Admin");
        }
        userRepository.save(rider);

        return ResponseEntity.ok(Map.of("success", true, "message", "Vehicle status set to " + normalizedStatus));
    }
}
