package com.example.demo.repository;

import com.example.demo.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find notifications for a user
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :userId ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByUserId(@Param("userId") Long userId);

    // Find unread notifications for a user
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :userId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadNotificationsByUserId(@Param("userId") Long userId);

    // Count unread notifications for a user
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient.id = :userId AND n.isRead = false")
    long countUnreadNotificationsByUserId(@Param("userId") Long userId);

    // Find notifications by type
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :userId AND n.type = :type ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByUserIdAndType(@Param("userId") Long userId, @Param("type") Notification.NotificationType type);

    // Find recent notifications (last 30 days)
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :userId AND n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    // Mark notification as read
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :notificationId")
    void markAsRead(@Param("notificationId") Long notificationId);

    // Mark all notifications as read for a user
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipient.id = :userId AND n.isRead = false")
    void markAllAsReadForUser(@Param("userId") Long userId);

    // Delete old notifications (older than specified date)
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :before")
    void deleteNotificationsBefore(@Param("before") LocalDateTime before);

    // Find notifications related to a contract
    @Query("SELECT n FROM Notification n WHERE n.relatedContractId = :contractId ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByContractId(@Param("contractId") Long contractId);

    // Find notifications related to a space
    @Query("SELECT n FROM Notification n WHERE n.relatedSpaceId = :spaceId ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsBySpaceId(@Param("spaceId") Long spaceId);
}