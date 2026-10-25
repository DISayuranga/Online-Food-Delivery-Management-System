package com.fooddelivery.controller;

import com.fooddelivery.model.*;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/tracking")
    public ResponseEntity<?> trackActiveOrders(@RequestParam String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            List<OrderRecord> allOrders = orderRepository.findByCustomerOrderByOrderDateDesc(customer);
            List<OrderRecord> activeOrders = allOrders.stream()
                    .filter(order -> !order.getOrderStatus().equals("DELIVERED"))
                    .collect(Collectors.toList());

            List<Map<String, Object>> result = activeOrders.stream().map(this::orderToMap).collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("orders", result, "role", "CUSTOMER"));

        } else if (user instanceof RestaurantOwner) {
            RestaurantOwner owner = (RestaurantOwner) user;
            List<OrderRecord> allOrders = orderRepository.findByRestaurantOrderByOrderDateDesc(owner);
            List<OrderRecord> activeOrders = allOrders.stream()
                    .filter(order -> !order.getOrderStatus().equals("DELIVERED"))
                    .collect(Collectors.toList());

            List<Map<String, Object>> result = activeOrders.stream().map(this::orderToMap).collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("orders", result, "role", "RESTAURANT_OWNER"));

        } else if (user instanceof Rider) {
            Rider rider = (Rider) user;
            List<OrderRecord> availableOrders = orderRepository.findByOrderStatusAndRiderIsNull("OUT_FOR_DELIVERY");
            List<OrderRecord> myDeliveries = orderRepository.findByRiderOrderByOrderDateDesc(rider).stream()
                    .filter(order -> !order.getOrderStatus().equals("DELIVERED"))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                "availableOrders", availableOrders.stream().map(this::orderToMap).collect(Collectors.toList()),
                "myDeliveries", myDeliveries.stream().map(this::orderToMap).collect(Collectors.toList()),
                "role", "RIDER"
            ));
        }
        
        return ResponseEntity.ok(Map.of("orders", List.of(), "role", "UNKNOWN"));
    }

    @GetMapping("/history")
    public ResponseEntity<?> viewOrderHistory(@RequestParam String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !(user instanceof Customer)) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        Customer customer = (Customer) user;
        List<OrderRecord> deliveredOrders = orderRepository.findByCustomerOrderByOrderDateDesc(customer)
                .stream()
                .filter(order -> order.getOrderStatus().equals("DELIVERED"))
                .collect(Collectors.toList());

        return ResponseEntity.ok(deliveredOrders.stream().map(this::orderToMap).collect(Collectors.toList()));
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, @RequestParam String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        OrderRecord order = orderRepository.findById(id).orElse(null);
        if (order == null) return ResponseEntity.badRequest().body(Map.of("error", "Order not found"));

        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            if (!order.getCustomer().getId().equals(customer.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Not your order"));
            }
            if ("PLACED".equals(order.getOrderStatus())) {
                order.setOrderStatus("CANCELLED");
                orderRepository.save(order);
                return ResponseEntity.ok(Map.of("success", true, "message", "Order successfully cancelled"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Cannot cancel order once restaurant has accepted or prepared it"));
            }
        }
        return ResponseEntity.status(403).body(Map.of("error", "Unauthorized"));
    }
    
    @PostMapping("/advance/{id}")
    public ResponseEntity<?> advanceStatus(@PathVariable Long id, @RequestParam String nextStatus, @RequestParam String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        OrderRecord order = orderRepository.findById(id).orElse(null);
        if (order == null) return ResponseEntity.badRequest().body(Map.of("error", "Order not found"));

        if (user instanceof RestaurantOwner) {
            RestaurantOwner owner = (RestaurantOwner) user;
            if (!order.getRestaurant().getId().equals(owner.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Unauthorized for this restaurant"));
            }
            if ("PREPARING".equals(nextStatus) || "OUT_FOR_DELIVERY".equals(nextStatus) || "CANCELLED".equals(nextStatus)) {
                order.setOrderStatus(nextStatus);
                orderRepository.save(order);
                return ResponseEntity.ok(Map.of("success", true));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid status transition for restaurant"));
            }
        } else if (user instanceof Rider) {
            Rider rider = (Rider) user;
            if ("CLAIM".equals(nextStatus) && order.getRider() == null && "OUT_FOR_DELIVERY".equals(order.getOrderStatus())) {
                if (!rider.isVehicleApproved()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Your delivery vehicle is pending Admin validation. Please wait until Admin approves your vehicle."));
                }
                order.setRider(rider);
                orderRepository.save(order);
                return ResponseEntity.ok(Map.of("success", true));
            } else if ("DELIVERED".equals(nextStatus) && order.getRider() != null && order.getRider().getId().equals(rider.getId())) {
                order.setOrderStatus("DELIVERED");
                orderRepository.save(order);
                return ResponseEntity.ok(Map.of("success", true));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Cannot claim or complete this order"));
            }
        }

        return ResponseEntity.status(403).body(Map.of("error", "Unauthorized role for this action"));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllOrdersForAdmin(@RequestParam String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (!(user instanceof Admin)) return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        List<OrderRecord> allOrders = orderRepository.findAll();
        allOrders.sort((a, b) -> b.getOrderDate() != null && a.getOrderDate() != null ? b.getOrderDate().compareTo(a.getOrderDate()) : 0);
        return ResponseEntity.ok(allOrders.stream().map(this::orderToMap).collect(Collectors.toList()));
    }

    @GetMapping("/restaurant/summary")
    public ResponseEntity<?> getRestaurantSummary(@RequestParam String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (!(user instanceof RestaurantOwner)) return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        RestaurantOwner owner = (RestaurantOwner) user;

        List<OrderRecord> allOrders = orderRepository.findByRestaurantOrderByOrderDateDesc(owner);
        double totalSales = 0.0;
        int completedCount = 0;
        int activeCount = 0;
        int cancelledCount = 0;

        for (OrderRecord o : allOrders) {
            if ("DELIVERED".equals(o.getOrderStatus())) {
                totalSales += o.getTotalAmount();
                completedCount++;
            } else if ("CANCELLED".equals(o.getOrderStatus())) {
                cancelledCount++;
            } else {
                activeCount++;
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("totalSales", totalSales);
        res.put("totalRevenue", totalSales);
        res.put("totalOrders", allOrders.size());
        res.put("completedOrders", completedCount);
        res.put("activeOrders", activeCount);
        res.put("cancelledOrders", cancelledCount);
        res.put("recentOrders", allOrders.stream().limit(10).map(this::orderToMap).collect(Collectors.toList()));
        res.put("allOrders", allOrders.stream().map(this::orderToMap).collect(Collectors.toList()));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/reports/restaurant-sales")
    public ResponseEntity<?> downloadRestaurantReport(@RequestParam String username) throws IOException {
        User user = userRepository.findByUsername(username).orElse(null);
        if (!(user instanceof RestaurantOwner)) return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        RestaurantOwner owner = (RestaurantOwner) user;

        List<OrderRecord> allOrders = orderRepository.findByRestaurantOrderByOrderDateDesc(owner);
        File reportsDir = new File("reports");
        if (!reportsDir.exists()) reportsDir.mkdirs();

        File reportFile = new File(reportsDir, "restaurant_sales_" + owner.getUsername() + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
            writer.write("==================================================\n");
            writer.write("          RESTAURANT SALES REPORT\n");
            writer.write("==================================================\n");
            writer.write("Restaurant: " + owner.getRestaurantName() + "\n");
            writer.write("Owner: " + owner.getFullName() + " (@" + owner.getUsername() + ")\n");
            writer.write("Total Orders: " + allOrders.size() + "\n");
            writer.write("==================================================\n\n");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a");
            double totalDeliveredRevenue = 0.0;

            for (OrderRecord o : allOrders) {
                writer.write("Order #" + o.getId() + " | Status: " + o.getOrderStatus() + "\n");
                writer.write("Date: " + (o.getOrderDate() != null ? o.getOrderDate().format(formatter) : "N/A") + "\n");
                writer.write("Customer: " + (o.getCustomer() != null ? o.getCustomer().getFullName() : "N/A") + "\n");
                writer.write("Items: " + o.getItemsSummary() + "\n");
                writer.write("Amount: Rs. " + String.format("%.2f", o.getTotalAmount()) + "\n");
                writer.write("--------------------------------------------------\n");
                if ("DELIVERED".equals(o.getOrderStatus())) {
                    totalDeliveredRevenue += o.getTotalAmount();
                }
            }

            writer.write("\nTotal Delivered Revenue: Rs. " + String.format("%.2f", totalDeliveredRevenue) + "\n");
        }

        org.springframework.core.io.FileSystemResource resource = new org.springframework.core.io.FileSystemResource(reportFile);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + reportFile.getName())
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(reportFile.length())
                .body(resource);
    }

    @GetMapping("/reports/rider-deliveries")
    public ResponseEntity<?> downloadRiderReport(@RequestParam String username) throws IOException {
        User user = userRepository.findByUsername(username).orElse(null);
        if (!(user instanceof Rider)) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }

        Rider rider = (Rider) user;
        List<OrderRecord> deliveredOrders = orderRepository.findByRiderOrderByOrderDateDesc(rider).stream()
                .filter(order -> "DELIVERED".equals(order.getOrderStatus()))
                .collect(Collectors.toList());

        // OOP/File Handling: Creating directory and file
        File reportsDir = new File("reports");
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }

        File reportFile = new File(reportsDir, "rider_deliveries_" + rider.getUsername() + ".txt");
        
        // Using FileWriter and BufferedWriter to write data to disk
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
            writer.write("==================================================\n");
            writer.write("          RIDER DELIVERY REPORT\n");
            writer.write("==================================================\n");
            writer.write("Rider: " + rider.getFullName() + " (@" + rider.getUsername() + ")\n");
            writer.write("Vehicle Plate: " + (rider.getVehiclePlateNumber() != null ? rider.getVehiclePlateNumber() : "N/A") + "\n");
            writer.write("Total Deliveries: " + deliveredOrders.size() + "\n");
            writer.write("==================================================\n\n");
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a");
            double totalEarnings = 0;

            for (OrderRecord order : deliveredOrders) {
                writer.write("Order ID: #" + order.getId() + "\n");
                writer.write("Date: " + (order.getOrderDate() != null ? order.getOrderDate().format(formatter) : "N/A") + "\n");
                writer.write("Customer: " + (order.getCustomer() != null ? order.getCustomer().getFullName() : "N/A") + "\n");
                writer.write("Delivery Address: " + (order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "N/A") + "\n");
                writer.write("Restaurant: " + (order.getRestaurant() != null ? order.getRestaurant().getRestaurantName() : "N/A") + "\n");
                writer.write("Order Total: Rs. " + String.format("%.2f", order.getTotalAmount()) + "\n");
                writer.write("--------------------------------------------------\n");
                totalEarnings += 250.0; // Reasonable delivery fee per trip in LKR
            }

            writer.write("\nEstimated Total Earnings (Rs. 250.00/delivery): Rs. " + String.format("%.2f", totalEarnings) + "\n");
        }

        // Using FileSystemResource for safe file streaming without unclosed stream leaks
        org.springframework.core.io.FileSystemResource resource = new org.springframework.core.io.FileSystemResource(reportFile);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + reportFile.getName())
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(reportFile.length())
                .body(resource);
    }

    private Map<String, Object> orderToMap(OrderRecord order) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("totalAmount", order.getTotalAmount());
        map.put("orderStatus", order.getOrderStatus());
        map.put("deliveryAddress", order.getDeliveryAddress());
        map.put("orderDate", order.getOrderDate() != null ? order.getOrderDate().toString() : "");
        map.put("itemsSummary", order.getItemsSummary());
        map.put("customerName", order.getCustomer().getFullName());
        map.put("customerPhone", order.getCustomer().getPhoneNumber() != null ? order.getCustomer().getPhoneNumber() : "N/A");
        map.put("restaurantName", order.getRestaurant().getRestaurantName());
        map.put("riderName", order.getRider() != null ? order.getRider().getFullName() : null);

        List<Map<String, Object>> itemsList = new ArrayList<>();
        if (order.getItemsJson() != null && !order.getItemsJson().trim().isEmpty()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                itemsList = mapper.readValue(order.getItemsJson(), new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {});
            } catch (Exception ignored) {}
        }
        if (itemsList.isEmpty() && order.getItemsSummary() != null) {
            String[] parts = order.getItemsSummary().split(",\\s*");
            for (String p : parts) {
                Map<String, Object> im = new HashMap<>();
                im.put("name", p);
                im.put("quantity", 1);
                im.put("price", 0.0);
                im.put("imageUrl", "");
                itemsList.add(im);
            }
        }
        map.put("itemsList", itemsList);

        return map;
    }
}
