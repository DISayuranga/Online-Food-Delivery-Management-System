package com.fooddelivery.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerUsername;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerEmail;

    @Column
    private String customerPhone;

    // Recipient details
    @Column(nullable = false)
    private String targetType; // "ADMIN", "RESTAURANT", "RIDER"

    @Column
    private Long targetId; // Restaurant owner ID or Rider ID (null for general Admin)

    @Column(nullable = false)
    private String targetName; // e.g. "CraveBite Admin / Support", "Bob's Burgers", "John Rider"

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Reply details
    @Column(columnDefinition = "TEXT")
    private String reply;

    @Column
    private String repliedByRole; // "ADMIN", "RESTAURANT", "RIDER"

    @Column
    private String repliedByName; // e.g. "Admin Alex", "Bob's Burgers Chef", "Rider Kamal"

    @Column
    private LocalDateTime repliedAt;

    @Column(nullable = false)
    private String status; // "PENDING", "REPLIED"

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerUsername() { return customerUsername; }
    public void setCustomerUsername(String customerUsername) { this.customerUsername = customerUsername; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public String getRepliedByRole() { return repliedByRole; }
    public void setRepliedByRole(String repliedByRole) { this.repliedByRole = repliedByRole; }

    public String getRepliedByName() { return repliedByName; }
    public void setRepliedByName(String repliedByName) { this.repliedByName = repliedByName; }

    public LocalDateTime getRepliedAt() { return repliedAt; }
    public void setRepliedAt(LocalDateTime repliedAt) { this.repliedAt = repliedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
