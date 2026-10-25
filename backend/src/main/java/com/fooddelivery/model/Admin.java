package com.fooddelivery.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    
    @Override
    public String getRoleName() {
        return "System Admin";
    }

    @Override
    public String getRoleType() {
        return "ADMIN";
    }
}
