

package com.fooddelivery.repository;

import com.fooddelivery.model.Customer;
import com.fooddelivery.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findByCustomer(Customer customer);
    Optional<PaymentMethod> findFirstByCustomerIdAndDefaultPaymentMethodTrue(Long customerId);
}
