package com.fooddelivery.repository;

import com.fooddelivery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Spring Data JPA provides Abstraction over database operations.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Custom query method abstracted by Spring Data JPA
    Optional<User> findByUsername(String username);
    
    java.util.List<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrUsernameContainingIgnoreCase(String fullName, String email, String username);

    @org.springframework.data.jpa.repository.Query("SELECT r FROM RestaurantOwner r")
    java.util.List<com.fooddelivery.model.RestaurantOwner> findAllRestaurantOwners();
}
