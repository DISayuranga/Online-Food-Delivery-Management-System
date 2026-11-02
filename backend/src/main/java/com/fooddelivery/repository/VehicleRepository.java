package com.fooddelivery.repository;

import com.fooddelivery.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByRiderUsernameOrderByCreatedAtDesc(String riderUsername);
    List<Vehicle> findAllByOrderByCreatedAtDesc();
}
