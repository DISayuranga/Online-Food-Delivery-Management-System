package com.fooddelivery.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

// OOP: Inheritance - Rider class extends the abstract User class.
@Entity
@DiscriminatorValue("RIDER")
public class Rider extends User {

    // OOP: Encapsulation
    private String phoneNumber;
    private String vehiclePlateNumber;
    private String vehicleType;           // BIKE, CAR, SCOOTER, THREE_WHEELER, VAN, BICYCLE
    private String vehicleModel;          // e.g. Honda Dio 110, Yamaha FZ
    private String vehicleColor;          // e.g. Matte Black
    private String vehicleImageUrl;       // /uploads/vehicle_...
    private String vehicleApprovalStatus; // APPROVED, PENDING_APPROVAL, REJECTED
    private String vehicleAdminNotes;     // Admin verification remarks

    // OOP: Polymorphism - Overriding base class method.
    @Override
    public String getRoleName() {
        return "Rider";
    }

    @Override
    public String getRoleType() {
        return "RIDER";
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

    public boolean isVehicleApproved() {
        return "APPROVED".equalsIgnoreCase(vehicleApprovalStatus);
    }

    public String getVehiclePlateNumber() {
        return vehiclePlateNumber;
    }

    public void setVehiclePlateNumber(String vehiclePlateNumber) {
        this.vehiclePlateNumber = vehiclePlateNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
