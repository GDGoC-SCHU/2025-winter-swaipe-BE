package com.donut.swaipe.domain.notification.repository;

import com.donut.swaipe.domain.notification.entity.Notification;
import com.donut.swaipe.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}