package com.fooddelivery.repository;

import com.fooddelivery.model.Customer;
import com.fooddelivery.model.OrderRecord;
import com.fooddelivery.model.RestaurantOwner;
import com.fooddelivery.model.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderRecord, Long> {
    List<OrderRecord> findByCustomerOrderByOrderDateDesc(Customer customer);
    List<OrderRecord> findByRestaurantOrderByOrderDateDesc(RestaurantOwner restaurant);
    List<OrderRecord> findByRiderOrderByOrderDateDesc(Rider rider);
    List<OrderRecord> findByOrderStatusAndRiderIsNull(String orderStatus);
}
