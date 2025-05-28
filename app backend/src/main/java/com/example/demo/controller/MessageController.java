package com.example.demo.controller;

import com.example.demo.dto.MessageDto;
import com.example.demo.model.Message;
import com.example.demo.service.MessageService;
import com.example.demo.service.NotificationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "http://localhost:3000")
public class MessageController {

    private final MessageService messageService;
    private final NotificationService notificationService;

    public MessageController(MessageService messageService, NotificationService notificationService) {
        this.messageService = messageService;
        this.notificationService = notificationService;
    }

    // REST endpoints for HTTP requests
    @PostMapping("/send")
    public ResponseEntity<MessageDto> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        log.info("Sending message from user {} to user {}", request.getSenderId(), request.getRecipientId());

        try {
            Message message = messageService.sendMessage(
                    request.getSenderId(),
                    request.getRecipientId(),
                    request.getContent(),
                    request.getMessageType(),
                    request.getRelatedContractId(),
                    request.getRelatedSpaceId()
            );

            // Create notification for new message
            notificationService.notifyNewMessage(
                    request.getRecipientId(),
                    message.getSenderName(),
                    message.getId()
            );

            return ResponseEntity.ok(messageService.getConversation(
                    request.getSenderId(), request.getRecipientId()).get(0));
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/conversation/{user1Id}/{user2Id}")
    public ResponseEntity<List<MessageDto>> getConversation(
            @PathVariable Long user1Id,
            @PathVariable Long user2Id) {
        log.info("Getting conversation between users {} and {}", user1Id, user2Id);

        try {
            List<MessageDto> messages = messageService.getConversation(user1Id, user2Id);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Error getting conversation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MessageDto>> getUserMessages(@PathVariable Long userId) {
        log.info("Getting all messages for user: {}", userId);

        try {
            List<MessageDto> messages = messageService.getUserMessages(userId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Error getting user messages: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<MessageDto>> getUnreadMessages(@PathVariable Long userId) {
        log.info("Getting unread messages for user: {}", userId);

        try {
            List<MessageDto> messages = messageService.getUnreadMessages(userId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Error getting unread messages: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<MessageDto>> getRecentConversations(@PathVariable Long userId) {
        log.info("Getting recent conversations for user: {}", userId);

        try {
            List<MessageDto> conversations = messageService.getRecentConversations(userId);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            log.error("Error getting recent conversations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/mark-read")
    public ResponseEntity<Void> markMessagesAsRead(@RequestBody MarkReadRequest request) {
        log.info("Marking messages as read for user {} from sender {}", request.getUserId(), request.getSenderId());

        try {
            messageService.markMessagesAsRead(request.getUserId(), request.getSenderId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error marking messages as read: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/mark-read/{messageId}")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable Long messageId) {
        log.info("Marking single message as read: {}", messageId);

        try {
            messageService.markMessageAsRead(messageId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error marking message as read: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/unread-count/{userId}")
    public ResponseEntity<Map<String, Long>> getUnreadMessagesCount(@PathVariable Long userId) {
        try {
            long count = messageService.getUnreadMessagesCount(userId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            log.error("Error getting unread messages count: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId, @RequestParam Long userId) {
        log.info("Deleting message {} by user {}", messageId, userId);

        try {
            messageService.deleteMessage(messageId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting message: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // WebSocket message handlers
    @MessageMapping("/send-message")
    public void handleWebSocketMessage(@Payload SendMessageRequest message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Received WebSocket message from user {} to user {}", message.getSenderId(), message.getRecipientId());

        try {
            Message sentMessage = messageService.sendMessage(
                    message.getSenderId(),
                    message.getRecipientId(),
                    message.getContent(),
                    message.getMessageType(),
                    message.getRelatedContractId(),
                    message.getRelatedSpaceId()
            );

            // Create notification for new message
            notificationService.notifyNewMessage(
                    message.getRecipientId(),
                    sentMessage.getSenderName(),
                    sentMessage.getId()
            );

            log.info("WebSocket message processed successfully");
        } catch (Exception e) {
            log.error("Error processing WebSocket message: {}", e.getMessage());
        }
    }

    @MessageMapping("/mark-read")
    public void handleMarkRead(@Payload MarkReadRequest request) {
        log.info("Received WebSocket mark-read from user {} for sender {}", request.getUserId(), request.getSenderId());

        try {
            messageService.markMessagesAsRead(request.getUserId(), request.getSenderId());
        } catch (Exception e) {
            log.error("Error processing WebSocket mark-read: {}", e.getMessage());
        }
    }

    // Request DTOs
    @lombok.Data
    public static class SendMessageRequest {
        private Long senderId;
        private Long recipientId;
        private String content;
        private String messageType;
        private Long relatedContractId;
        private Long relatedSpaceId;
    }

    @lombok.Data
    public static class MarkReadRequest {
        private Long userId;
        private Long senderId;
    }
}