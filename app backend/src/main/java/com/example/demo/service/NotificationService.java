package com.example.demo.service;

import com.example.demo.dto.NotificationDto;
import com.example.demo.model.Notification;
import java.util.List;

public interface NotificationService {
    // Create a new notification
    Notification createNotification(Long recipientId, String title, String message,
                                    Notification.NotificationType type, String actionUrl,
                                    Long relatedContractId, Long relatedSpaceId, Long relatedUserId);

    // Get all notifications for a user
    List<NotificationDto> getUserNotifications(Long userId);

    // Get unread notifications for a user
    List<NotificationDto> getUnreadNotifications(Long userId);

    // Get notifications count for a user
    long getUnreadNotificationsCount(Long userId);

    // Mark notification as read
    void markNotificationAsRead(Long notificationId);

    // Mark all notifications as read for a user
    void markAllNotificationsAsRead(Long userId);

    // Delete notification
    void deleteNotification(Long notificationId, Long userId);

    // Get recent notifications (last 30 days)
    List<NotificationDto> getRecentNotifications(Long userId);

    // Cleanup old notifications (older than 3 months)
    void cleanupOldNotifications();

    // Helper methods for creating specific notification types
    void notifyContractCreated(Long ownerId, Long tenantId, Long contractId, String spaceName);
    void notifyContractExpiring(Long tenantId, Long contractId, String spaceName, int daysUntilExpiry);
    void notifyContractTerminated(Long ownerId, Long tenantId, Long contractId, String spaceName);
    void notifyNewMessage(Long recipientId, String senderName, Long messageId);
    void notifyPaymentDue(Long tenantId, Long contractId, String spaceName, double amount);
}