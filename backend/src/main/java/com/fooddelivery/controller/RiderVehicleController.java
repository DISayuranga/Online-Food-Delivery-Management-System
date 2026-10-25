package com.fooddelivery.controller;

import com.fooddelivery.model.Rider;
import com.fooddelivery.model.User;
import com.fooddelivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/rider/vehicle")
public class RiderVehicleController {

    @Autowired
    private UserRepository userRepository;

    private Rider getRider(String username) {
        if (username == null || username.trim().isEmpty()) return null;
        User user = userRepository.findByUsername(username).orElse(null);
        return (user instanceof Rider) ? (Rider) user : null;
    }

    @GetMapping
    public ResponseEntity<?> getVehicleDetails(@RequestParam String username) {
        Rider rider = getRider(username);
        if (rider == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized or not a Rider"));

        Map<String, Object> details = new HashMap<>();
        details.put("username", rider.getUsername());
        details.put("fullName", rider.getFullName());
        details.put("phoneNumber", rider.getPhoneNumber() != null ? rider.getPhoneNumber() : "");
        details.put("vehicleType", rider.getVehicleType() != null ? rider.getVehicleType() : "BIKE");
        details.put("vehicleTypeDisplay", rider.getVehicleTypeDisplay());
        details.put("vehiclePlateNumber", rider.getVehiclePlateNumber() != null ? rider.getVehiclePlateNumber() : "");
        details.put("vehicleModel", rider.getVehicleModel() != null ? rider.getVehicleModel() : "");
        details.put("vehicleColor", rider.getVehicleColor() != null ? rider.getVehicleColor() : "");
        details.put("vehicleImageUrl", rider.getVehicleImageUrl() != null ? rider.getVehicleImageUrl() : "");
        
        String status = rider.getVehicleApprovalStatus();
        if (status == null || status.trim().isEmpty()) {
            status = (rider.getVehiclePlateNumber() != null && !rider.getVehiclePlateNumber().isEmpty()) ? "APPROVED" : "PENDING_APPROVAL";
        }
        details.put("vehicleApprovalStatus", status);
        details.put("vehicleAdminNotes", rider.getVehicleAdminNotes() != null ? rider.getVehicleAdminNotes() : "");
        details.put("isApproved", "APPROVED".equalsIgnoreCase(status));

        return ResponseEntity.ok(details);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveVehicleDetails(
            @RequestParam String username,
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

        rider.setVehicleType(vehicleType != null ? vehicleType.trim().toUpperCase() : "BIKE");
        rider.setVehiclePlateNumber(vehiclePlateNumber.trim());
        rider.setVehicleModel(vehicleModel != null ? vehicleModel.trim() : "");
        rider.setVehicleColor(vehicleColor != null ? vehicleColor.trim() : "");

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
                Path target1 = Paths.get("uploads", filename);
                Files.write(target1, vehicleImage.getBytes());

                File uploadDir2 = new File("frontend/uploads");
                if (!uploadDir2.exists()) uploadDir2.mkdirs();
                Path target2 = Paths.get("frontend/uploads", filename);
                Files.write(target2, vehicleImage.getBytes());

                rider.setVehicleImageUrl("/uploads/" + filename);
            } catch (IOException e) {
                return ResponseEntity.status(500).body(Map.of("error", "Failed to save vehicle image: " + e.getMessage()));
            }
        }

        // Submitting/updating vehicle requires Admin validation and approval
        rider.setVehicleApprovalStatus("PENDING_APPROVAL");
        rider.setVehicleAdminNotes("Awaiting Admin verification");
        userRepository.save(rider);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Vehicle details submitted successfully! Awaiting Admin verification.",
                "vehicleApprovalStatus", "PENDING_APPROVAL"
        ));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteVehicleDetails(@RequestParam String username) {
        Rider rider = getRider(username);
        if (rider == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        rider.setVehiclePlateNumber(null);
        rider.setVehicleType(null);
        rider.setVehicleModel(null);
        rider.setVehicleColor(null);
        rider.setVehicleImageUrl(null);
        rider.setVehicleApprovalStatus(null);
        rider.setVehicleAdminNotes(null);
        userRepository.save(rider);

        return ResponseEntity.ok(Map.of("success", true, "message", "Vehicle profile deleted"));
    }
}
