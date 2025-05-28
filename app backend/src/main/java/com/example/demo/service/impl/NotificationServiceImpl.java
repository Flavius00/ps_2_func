package com.example.demo.service.impl;

import com.example.demo.dto.NotificationDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.InsufficientPermissionsException;
import com.example.demo.mapper.NotificationMapper;
import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    public Notification createNotification(Long recipientId, String title, String message,
                                           Notification.NotificationType type, String actionUrl,
                                           Long relatedContractId, Long relatedSpaceId, Long relatedUserId) {
        log.info("Creating notification for user {}: {}", recipientId, title);

        // Validate recipient exists
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found with id: " + recipientId));

        // Validate input
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification title cannot be empty");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification message cannot be empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Notification type cannot be null");
        }

        // Create notification
        Notification notification = Notification.builder()
                .recipient(recipient)
                .title(title.trim())
                .message(message.trim())
                .type(type)
                .actionUrl(actionUrl)
                .relatedContractId(relatedContractId)
                .relatedSpaceId(relatedSpaceId)
                .relatedUserId(relatedUserId)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification created with ID: {}", savedNotification.getId());

        return savedNotification;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(Long userId) {
        log.info("Getting all notifications for user: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<Notification> notifications = notificationRepository.findNotificationsByUserId(userId);
        return notifications.stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(Long userId) {
        log.info("Getting unread notifications for user: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<Notification> notifications = notificationRepository.findUnreadNotificationsByUserId(userId);
        return notifications.stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadNotificationsCount(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return notificationRepository.countUnreadNotificationsByUserId(userId);
    }

    @Override
    public void markNotificationAsRead(Long notificationId) {
        log.info("Marking notification as read: {}", notificationId);

        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("Notification not found with id: " + notificationId);
        }

        notificationRepository.markAsRead(notificationId);
    }

    @Override
    public void markAllNotificationsAsRead(Long userId) {
        log.info("Marking all notifications as read for user: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        notificationRepository.markAllAsReadForUser(userId);
    }

    @Override
    public void deleteNotification(Long notificationId, Long userId) {
        log.info("Deleting notification {} by user {}", notificationId, userId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        // Only recipient can delete their own notifications
        if (!notification.getRecipientId().equals(userId)) {
            throw new InsufficientPermissionsException("You can only delete your own notifications");
        }

        notificationRepository.deleteById(notificationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getRecentNotifications(Long userId) {
        log.info("Getting recent notifications for user: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Notification> notifications = notificationRepository.findRecentNotifications(userId, thirtyDaysAgo);

        return notifications.stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void cleanupOldNotifications() {
        log.info("Cleaning up old notifications");

        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        notificationRepository.deleteNotificationsBefore(threeMonthsAgo);

        log.info("Old notifications cleanup completed");
    }

    // Helper methods for specific notification types
    @Override
    public void notifyContractCreated(Long ownerId, Long tenantId, Long contractId, String spaceName) {
        // Notify owner
        createNotification(
                ownerId,
                "New Contract Created",
                "A new rental contract has been created for your space: " + spaceName,
                Notification.NotificationType.CONTRACT_CREATED,
                "/contracts/" + contractId,
                contractId,
                null,
                tenantId
        );

        // Notify tenant
        createNotification(
                tenantId,
                "Contract Confirmed",
                "Your rental contract for " + spaceName + " has been confirmed",
                Notification.NotificationType.CONTRACT_CREATED,
                "/contracts/" + contractId,
                contractId,
                null,
                ownerId
        );
    }

    @Override
    public void notifyContractExpiring(Long tenantId, Long contractId, String spaceName, int daysUntilExpiry) {
        createNotification(
                tenantId,
                "Contract Expiring Soon",
                "Your rental contract for " + spaceName + " will expire in " + daysUntilExpiry + " days",
                Notification.NotificationType.CONTRACT_EXPIRING,
                "/contracts/" + contractId,
                contractId,
                null,
                null
        );
    }

    @Override
    public void notifyContractTerminated(Long ownerId, Long tenantId, Long contractId, String spaceName) {
        // Notify owner
        createNotification(
                ownerId,
                "Contract Terminated",
                "The rental contract for your space " + spaceName + " has been terminated",
                Notification.NotificationType.CONTRACT_TERMINATED,
                "/contracts/" + contractId,
                contractId,
                null,
                tenantId
        );

        // Notify tenant
        createNotification(
                tenantId,
                "Contract Terminated",
                "Your rental contract for " + spaceName + " has been terminated",
                Notification.NotificationType.CONTRACT_TERMINATED,
                "/contracts/" + contractId,
                contractId,
                null,
                ownerId
        );
    }

    @Override
    public void notifyNewMessage(Long recipientId, String senderName, Long messageId) {
        createNotification(
                recipientId,
                "New Message",
                "You have received a new message from " + senderName,
                Notification.NotificationType.MESSAGE_RECEIVED,
                "/messages",
                null,
                null,
                null
        );
    }

    @Override
    public void notifyPaymentDue(Long tenantId, Long contractId, String spaceName, double amount) {
        createNotification(
                tenantId,
                "Payment Due",
                "Your monthly rent of €" + amount + " for " + spaceName + " is due",
                Notification.NotificationType.PAYMENT_DUE,
                "/contracts/" + contractId,
                contractId,
                null,
                null
        );
    }
}