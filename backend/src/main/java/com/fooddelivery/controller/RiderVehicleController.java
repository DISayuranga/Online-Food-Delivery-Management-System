package com.fooddelivery.controller;

import com.fooddelivery.model.Rider;
import com.fooddelivery.model.User;
import com.fooddelivery.model.Vehicle;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/rider/vehicle")
public class RiderVehicleController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private Rider getRider(String username) {
        if (username == null || username.trim().isEmpty()) return null;
        User user = userRepository.findByUsername(username).orElse(null);
        return (user instanceof Rider) ? (Rider) user : null;
    }

    @GetMapping
    public ResponseEntity<?> getVehicleDetails(@RequestParam String username) {
        Rider rider = getRider(username);
        if (rider == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized or not a Rider"));

        List<Vehicle> vehicles = vehicleRepository.findByRiderUsernameOrderByCreatedAtDesc(rider.getUsername());

        // Auto-seed legacy rider vehicle if rider has vehicle plate but no Vehicle records yet
        if (vehicles.isEmpty() && rider.getVehiclePlateNumber() != null && !rider.getVehiclePlateNumber().isEmpty()) {
            Vehicle seed = new Vehicle(
                    rider.getUsername(),
                    rider.getVehicleType() != null ? rider.getVehicleType() : "BIKE",
                    rider.getVehiclePlateNumber(),
                    rider.getVehicleModel() != null ? rider.getVehicleModel() : "Primary Vehicle",
                    rider.getVehicleColor() != null ? rider.getVehicleColor() : "",
                    rider.getVehicleImageUrl() != null ? rider.getVehicleImageUrl() : ""
            );
            seed.setVehicleApprovalStatus(rider.getVehicleApprovalStatus() != null ? rider.getVehicleApprovalStatus() : "APPROVED");
            seed.setVehicleAdminNotes(rider.getVehicleAdminNotes() != null ? rider.getVehicleAdminNotes() : "Initial vehicle");
            vehicleRepository.save(seed);
            vehicles = List.of(seed);
        }

        boolean hasApprovedVehicle = vehicles.stream().anyMatch(Vehicle::isApproved);

        Map<String, Object> details = new HashMap<>();
        details.put("username", rider.getUsername());
        details.put("fullName", rider.getFullName());
        details.put("phoneNumber", rider.getPhoneNumber() != null ? rider.getPhoneNumber() : "");
        details.put("vehicles", vehicles);
        details.put("totalVehicles", vehicles.size());
        details.put("isApproved", hasApprovedVehicle);

        return ResponseEntity.ok(details);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listMyVehicles(@RequestParam String username) {
        return getVehicleDetails(username);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveVehicleDetails(
            @RequestParam String username,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam String vehicleType,
            @RequestParam String vehiclePlateNumber,
            @RequestParam String vehicleModel,
            @RequestParam(required = false) String vehicleColor,
            @RequestParam(value = "vehicleImage", required = false) MultipartFile vehicleImage) {

        Rider rider = getRider(username);
        if (rider == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized or not a Rider"));

        if (vehiclePlateNumber == null || vehiclePlateNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vehicle registration plate number is required"));
        }

        Vehicle vehicle;
        if (vehicleId != null) {
            vehicle = vehicleRepository.findById(vehicleId).orElse(null);
            if (vehicle == null || !vehicle.getRiderUsername().equals(rider.getUsername())) {
                return ResponseEntity.status(404).body(Map.of("error", "Vehicle not found"));
            }
        } else {
            vehicle = new Vehicle();
            vehicle.setRiderUsername(rider.getUsername());
        }

        vehicle.setVehicleType(vehicleType != null ? vehicleType.trim().toUpperCase() : "BIKE");
        vehicle.setVehiclePlateNumber(vehiclePlateNumber.trim());
        vehicle.setVehicleModel(vehicleModel != null ? vehicleModel.trim() : "");
        vehicle.setVehicleColor(vehicleColor != null ? vehicleColor.trim() : "");

        if (vehicleImage != null && !vehicleImage.isEmpty()) {
            try {
                String originalFilename = vehicleImage.getOriginalFilename();
                String ext = ".jpg";
                if (originalFilename != null && originalFilename.contains(".")) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String filename = "vehicle_" + System.currentTimeMillis() + ext;

                File uploadDir1 = new File("uploads");
                if (!uploadDir1.exists()) uploadDir1.mkdirs();
                Path target1 = uploadDir1.toPath().resolve(filename);
                Files.write(target1, vehicleImage.getBytes());

                File uploadDir2 = new File("frontend/uploads").exists() ? new File("frontend/uploads") : new File("../frontend/uploads");
                if (!uploadDir2.exists()) uploadDir2.mkdirs();
                Path target2 = uploadDir2.toPath().resolve(filename);
                Files.write(target2, vehicleImage.getBytes());

                vehicle.setVehicleImageUrl("/uploads/" + filename);
            } catch (IOException e) {
                return ResponseEntity.status(500).body(Map.of("error", "Failed to save vehicle image: " + e.getMessage()));
            }
        }

        // IMPORTANT REQUIREMENT: Adding or Updating a vehicle ALWAYS requires Admin verification and approval
        vehicle.setVehicleApprovalStatus("PENDING_APPROVAL");
        vehicle.setVehicleAdminNotes("Awaiting Admin verification");
        vehicleRepository.save(vehicle);

        // Also sync primary fields on rider object
        rider.setVehicleType(vehicle.getVehicleType());
        rider.setVehiclePlateNumber(vehicle.getVehiclePlateNumber());
        rider.setVehicleModel(vehicle.getVehicleModel());
        rider.setVehicleColor(vehicle.getVehicleColor());
        if (vehicle.getVehicleImageUrl() != null) {
            rider.setVehicleImageUrl(vehicle.getVehicleImageUrl());
        }
        userRepository.save(rider);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Vehicle saved successfully! Awaiting Admin approval.",
                "vehicle", vehicle
        ));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteVehicleDetails(@RequestParam String username, @RequestParam(required = false) Long vehicleId) {
        Rider rider = getRider(username);
        if (rider == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        // IMPORTANT REQUIREMENT: Deleting a vehicle requires NO admin approval and is COMPLETELY deleted
        if (vehicleId != null) {
            Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
            if (vehicle != null && vehicle.getRiderUsername().equals(rider.getUsername())) {
                vehicleRepository.delete(vehicle);
            }
        } else {
            // Delete all vehicles of rider if no specific ID passed
            List<Vehicle> myVehicles = vehicleRepository.findByRiderUsernameOrderByCreatedAtDesc(rider.getUsername());
            vehicleRepository.deleteAll(myVehicles);
        }

        // Check if any vehicles remain
        List<Vehicle> remaining = vehicleRepository.findByRiderUsernameOrderByCreatedAtDesc(rider.getUsername());
        if (remaining.isEmpty()) {
            rider.setVehiclePlateNumber(null);
            rider.setVehicleType(null);
            rider.setVehicleModel(null);
            rider.setVehicleColor(null);
            rider.setVehicleImageUrl(null);
            rider.setVehicleApprovalStatus(null);
            rider.setVehicleAdminNotes(null);
        } else {
            Vehicle next = remaining.get(0);
            rider.setVehiclePlateNumber(next.getVehiclePlateNumber());
            rider.setVehicleType(next.getVehicleType());
            rider.setVehicleModel(next.getVehicleModel());
            rider.setVehicleColor(next.getVehicleColor());
            rider.setVehicleImageUrl(next.getVehicleImageUrl());
            rider.setVehicleApprovalStatus(next.getVehicleApprovalStatus());
            rider.setVehicleAdminNotes(next.getVehicleAdminNotes());
        }
        userRepository.save(rider);

        return ResponseEntity.ok(Map.of("success", true, "message", "Vehicle completely deleted"));
    }
}
