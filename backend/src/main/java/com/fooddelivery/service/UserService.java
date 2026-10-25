package com.fooddelivery.service;

import com.fooddelivery.model.*;
import com.fooddelivery.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Transactional
    public boolean deleteUserCascaded(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            cartItemRepository.deleteAll(cartItemRepository.findByCustomer(customer));
            reviewRepository.deleteAll(reviewRepository.findByCustomer(customer));
            addressRepository.deleteAll(addressRepository.findByCustomer(customer));
            paymentMethodRepository.deleteAll(paymentMethodRepository.findByCustomer(customer));
            orderRepository.deleteAll(orderRepository.findByCustomerOrderByOrderDateDesc(customer));
        } else if (user instanceof RestaurantOwner) {
            RestaurantOwner owner = (RestaurantOwner) user;
            List<FoodItem> foodItems = foodItemRepository.findByOwner(owner);
            for (FoodItem item : foodItems) {
                cartItemRepository.deleteAll(cartItemRepository.findByFoodItem(item));
                reviewRepository.deleteAll(reviewRepository.findByFoodItem(item));
                foodItemRepository.delete(item);
            }
            orderRepository.deleteAll(orderRepository.findByRestaurantOrderByOrderDateDesc(owner));
        } else if (user instanceof Rider) {
            Rider rider = (Rider) user;
            reviewRepository.deleteAll(reviewRepository.findByRider(rider));
            List<OrderRecord> orders = orderRepository.findByRiderOrderByOrderDateDesc(rider);
            for (OrderRecord order : orders) {
                order.setRider(null);
                orderRepository.save(order);
            }
        }

        userRepository.deleteById(userId);
        return true;
    }
}
