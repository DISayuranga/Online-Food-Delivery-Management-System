package com.fooddelivery.controller;

import com.fooddelivery.model.*;
import com.fooddelivery.repository.AddressRepository;
import com.fooddelivery.repository.CartItemRepository;
import com.fooddelivery.repository.PaymentMethodRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private com.fooddelivery.repository.OrderRepository orderRepository;

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

    @GetMapping("/methods")
    public ResponseEntity<?> listPaymentMethods(@RequestParam String username) {
        Customer customer = getCustomerByUsername(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        List<PaymentMethod> methods = paymentMethodRepository.findByCustomer(customer);
        return ResponseEntity.ok(methods);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addPaymentMethod(@RequestParam String username,
                                              @RequestParam String cardType,
                                              @RequestParam String cardholderName,
                                              @RequestParam String cardNumber,
                                              @RequestParam String expiryDate,
                                              @RequestParam String cvv,
                                              @RequestParam(required = false) String specificDetails) {
        Customer customer = getCustomerByUsername(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        PaymentMethod newMethod;
        if ("CREDIT".equals(cardType)) {
            CreditCard credit = new CreditCard();
            try {
                credit.setCreditLimit(Double.parseDouble(specificDetails));
            } catch (Exception e) {
                credit.setCreditLimit(5000.0);
            }
            newMethod = credit;
        } else if ("DEBIT".equals(cardType)) {
            DebitCard debit = new DebitCard();
            debit.setBankName(specificDetails);
            newMethod = debit;
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid card type"));
        }

        newMethod.setCardholderName(cardholderName);
        newMethod.setCardNumber(cardNumber);
        newMethod.setExpiryDate(expiryDate);
        newMethod.setCvv(cvv);
        newMethod.setCustomer(customer);

        paymentMethodRepository.save(newMethod);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deletePaymentMethod(@PathVariable Long id, @RequestParam String username) {
        Customer customer = getCustomerByUsername(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        PaymentMethod method = paymentMethodRepository.findById(id).orElse(null);
        if (method != null && method.getCustomer().getId().equals(customer.getId())) {
            paymentMethodRepository.delete(method);
        }
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<?> showEditPaymentForm(@PathVariable Long id, @RequestParam String username) {
        Customer customer = getCustomerByUsername(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        PaymentMethod method = paymentMethodRepository.findById(id).orElse(null);
        if (method == null || !method.getCustomer().getId().equals(customer.getId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Not found"));
        }

        return ResponseEntity.ok(method);
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<?> editPaymentMethod(@PathVariable Long id,
                                               @RequestParam String username,
                                               @RequestParam String cardholderName,
                                               @RequestParam String cardNumber,
                                               @RequestParam String expiryDate,
                                               @RequestParam String cvv,
                                               @RequestParam(required = false) String specificDetails) {
        Customer customer = getCustomerByUsername(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        PaymentMethod method = paymentMethodRepository.findById(id).orElse(null);
        if (method != null && method.getCustomer().getId().equals(customer.getId())) {
            method.setCardholderName(cardholderName);
            method.setCardNumber(cardNumber);
            method.setExpiryDate(expiryDate);
            method.setCvv(cvv);

            if (method instanceof CreditCard) {
                try {
                    ((CreditCard) method).setCreditLimit(Double.parseDouble(specificDetails));
                } catch (Exception e) {
                    ((CreditCard) method).setCreditLimit(5000.0);
                }
            } else if (method instanceof DebitCard) {
                ((DebitCard) method).setBankName(specificDetails);
            }

            paymentMethodRepository.save(method);
        }
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/checkout")
    public ResponseEntity<?> showCheckout(@RequestParam String username) {
        Customer customer = getCustomerByUsername(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);
        double subTotal = cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum();
        
        List<Map<String, Object>> formattedCartItems = cartItems.stream().map(ci -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", ci.getId());
            map.put("quantity", ci.getQuantity());
            map.put("totalPrice", ci.getTotalPrice());

            Map<String, Object> foodMap = new HashMap<>();
            FoodItem fi = ci.getFoodItem();
            if (fi != null) {
                foodMap.put("id", fi.getId());
                foodMap.put("name", fi.getName());
                foodMap.put("basePrice", fi.getBasePrice());
                foodMap.put("finalPrice", fi.getFinalPrice());
            }
            map.put("foodItem", foodMap);
            return map;
        }).collect(Collectors.toList());

        List<PaymentMethod> methods = paymentMethodRepository.findByCustomer(customer);
        List<Address> addresses = addressRepository.findByCustomer(customer);
        
        Map<String, Object> response = new HashMap<>();
        response.put("cartItems", formattedCartItems);
        response.put("subTotal", subTotal);
        response.put("total", subTotal > 0 ? subTotal : 0.0);
        response.put("paymentMethods", methods);
        response.put("addresses", addresses);
        
        return ResponseEntity.ok(response);
    }

    @org.springframework.transaction.annotation.Transactional
    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestParam String username,
                                            @RequestParam Long paymentMethodId, 
                                            @RequestParam String deliveryAddress,
                                            @RequestParam double amount) {
        Customer customer = getCustomerByUsername(username);
        if (customer == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Your cart is empty"));
        }

        PaymentMethod method = paymentMethodRepository.findById(paymentMethodId).orElse(null);
        if (method == null || !method.getCustomer().getId().equals(customer.getId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid payment method"));
        }

        // OOP Polymorphic Payment processing
        String result = method.processPayment(amount);

        Map<RestaurantOwner, List<CartItem>> itemsByRestaurant = cartItems.stream()
            .collect(Collectors.groupingBy(item -> item.getFoodItem().getOwner()));

        for (Map.Entry<RestaurantOwner, List<CartItem>> entry : itemsByRestaurant.entrySet()) {
            RestaurantOwner restaurant = entry.getKey();
            List<CartItem> restaurantItems = entry.getValue();

            double totalForRestaurant = restaurantItems.stream().mapToDouble(CartItem::getTotalPrice).sum();
            String itemsSummary = restaurantItems.stream()
                .map(item -> item.getQuantity() + "x " + item.getFoodItem().getName())
                .collect(Collectors.joining(", "));

            List<Map<String, Object>> itemsList = new ArrayList<>();
            for (CartItem ci : restaurantItems) {
                Map<String, Object> im = new HashMap<>();
                im.put("name", ci.getFoodItem().getName());
                im.put("quantity", ci.getQuantity());
                im.put("price", ci.getTotalPrice());
                im.put("imageUrl", ci.getFoodItem().getImageUrl());
                im.put("itemType", ci.getFoodItem().getItemType());
                itemsList.add(im);
            }
            String itemsJson = "";
            try {
                itemsJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(itemsList);
            } catch (Exception ignored) {}

            OrderRecord order = new OrderRecord();
            order.setCustomer(customer);
            order.setRestaurant(restaurant);
            order.setTotalAmount(totalForRestaurant);
            order.setItemsSummary(itemsSummary);
            order.setItemsJson(itemsJson);
            order.setOrderStatus("PLACED");
            order.setDeliveryAddress(deliveryAddress != null && !deliveryAddress.trim().isEmpty() ? deliveryAddress : "Default Address");
            order.setOrderDate(LocalDateTime.now());
            
            orderRepository.save(order);
        }

        cartItemRepository.deleteAll(cartItems);
        
        return ResponseEntity.ok(Map.of("success", true, "message", "Payment Successful!", "details", result));
    }
}
