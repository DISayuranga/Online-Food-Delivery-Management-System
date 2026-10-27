package com.fooddelivery.repository;

import com.fooddelivery.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    List<ContactMessage> findByCustomerUsernameOrderByCreatedAtDesc(String customerUsername);
    List<ContactMessage> findAllByOrderByCreatedAtDesc();
    List<ContactMessage> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(String targetType, Long targetId);
    List<ContactMessage> findByTargetTypeOrderByCreatedAtDesc(String targetType);
}
