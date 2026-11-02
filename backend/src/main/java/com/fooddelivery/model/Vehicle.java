package com.fooddelivery.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rider_vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String riderUsername;

    @Column(nullable = false)
    private String vehicleType; // BIKE, SCOOTER, CAR, THREE_WHEELER, VAN, BICYCLE

    @Column(nullable = false)
    private String vehiclePlateNumber;

    private String vehicleModel;
    private String vehicleColor;
    private String vehicleImageUrl;

    @Column(nullable = false)
    private String vehicleApprovalStatus = "PENDING_APPROVAL"; // PENDING_APPROVAL, APPROVED, REJECTED

    private String vehicleAdminNotes = "Awaiting Admin verification";

    private LocalDateTime createdAt = LocalDateTime.now();

    public Vehicle() {}

    public Vehicle(String riderUsername, String vehicleType, String vehiclePlateNumber, String vehicleModel, String vehicleColor, String vehicleImageUrl) {
        this.riderUsername = riderUsername;
        this.vehicleType = vehicleType;
        this.vehiclePlateNumber = vehiclePlateNumber;
        this.vehicleModel = vehicleModel;
        this.vehicleColor = vehicleColor;
        this.vehicleImageUrl = vehicleImageUrl;
        this.vehicleApprovalStatus = "PENDING_APPROVAL";
        this.vehicleAdminNotes = "Awaiting Admin verification";
        this.createdAt = LocalDateTime.now();
    }

    public String getVehicleTypeDisplay() {
        if (vehicleType == null) return "🏍️ Motorcycle / Bike";
        switch (vehicleType.toUpperCase()) {
            case "CAR": return "🚗 Car";
            case "SCOOTER": return "🛵 Scooter";
            case "THREE_WHEELER": return "🛺 Three-Wheeler";
            case "VAN": return "🚐 Van";
            case "BICYCLE": return "🚲 Bicycle";
            default: return "🏍️ Motorcycle / Bike";
        }
    }

    public boolean isApproved() {
        return "APPROVED".equalsIgnoreCase(vehicleApprovalStatus);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRiderUsername() {
        return riderUsername;
    }

    public void setRiderUsername(String riderUsername) {
        this.riderUsername = riderUsername;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehiclePlateNumber() {
        return vehiclePlateNumber;
    }

    public void setVehiclePlateNumber(String vehiclePlateNumber) {
        this.vehiclePlateNumber = vehiclePlateNumber;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public String getVehicleImageUrl() {
        return vehicleImageUrl;
    }

    public void setVehicleImageUrl(String vehicleImageUrl) {
        this.vehicleImageUrl = vehicleImageUrl;
    }

    public String getVehicleApprovalStatus() {
        return vehicleApprovalStatus;
    }

    public void setVehicleApprovalStatus(String vehicleApprovalStatus) {
        this.vehicleApprovalStatus = vehicleApprovalStatus;
    }

    public String getVehicleAdminNotes() {
        return vehicleAdminNotes;
    }

    public void setVehicleAdminNotes(String vehicleAdminNotes) {
        this.vehicleAdminNotes = vehicleAdminNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
