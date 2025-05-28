package com.example.demo.service.impl;

import com.example.demo.dto.MessageDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.InsufficientPermissionsException;
import com.example.demo.mapper.MessageMapper;
import com.example.demo.model.Message;
import com.example.demo.model.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageServiceImpl(MessageRepository messageRepository,
                              UserRepository userRepository,
                              MessageMapper messageMapper,
                              SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messageMapper = messageMapper;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public Message sendMessage(Long senderId, Long recipientId, String content, String messageType,
                               Long relatedContractId, Long relatedSpaceId) {
        log.info("Sending message from user {} to user {}", senderId, recipientId);

        // Validate users exist
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found with id: " + senderId));

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found with id: " + recipientId));

        // Validate content
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        if (content.length() > 2000) {
            throw new IllegalArgumentException("Message content cannot exceed 2000 characters");
        }

        // Create message
        Message message = Message.builder()
                .sender(sender)
                .recipient(recipient)
                .content(content.trim())
                .messageType(parseMessageType(messageType))
                .relatedContractId(relatedContractId)
                .relatedSpaceId(relatedSpaceId)
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();

        Message savedMessage = messageRepository.save(message);
        log.info("Message saved with ID: {}", savedMessage.getId());

        // Send WebSocket notification to recipient
        try {
            MessageDto messageDto = messageMapper.toDto(savedMessage);
            messagingTemplate.convertAndSendToUser(
                    recipientId.toString(),
                    "/queue/messages",
                    messageDto
            );
            log.info("WebSocket message sent to user: {}", recipientId);
        } catch (Exception e) {
            log.error("Failed to send WebSocket message to user {}: {}", recipientId, e.getMessage());
            // Don't fail the entire operation if WebSocket fails
        }

        return savedMessage;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getConversation(Long user1Id, Long user2Id) {
        log.info("Getting conversation between users {} and {}", user1Id, user2Id);

        // Validate users exist
        if (!userRepository.existsById(user1Id)) {
            throw new ResourceNotFoundException("User not found with id: " + user1Id);
        }
        if (!userRepository.existsById(user2Id)) {
            throw new ResourceNotFoundException("User not found with id: " + user2Id);
        }

        List<Message> messages = messageRepository.findConversationBetweenUsers(user1Id, user2Id);
        return messages.stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getUserMessages(Long userId) {
        log.info("Getting all messages for user: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<Message> messages = messageRepository.findMessagesByUserId(userId);
        return messages.stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getUnreadMessages(Long userId) {
        log.info("Getting unread messages for user: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<Message> messages = messageRepository.findUnreadMessagesByUserId(userId);
        return messages.stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getRecentConversations(Long userId) {
        log.info("Getting recent conversations for user: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<Message> messages = messageRepository.findRecentConversations(userId);
        return messages.stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markMessagesAsRead(Long userId, Long senderId) {
        log.info("Marking messages as read for user {} from sender {}", userId, senderId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        if (!userRepository.existsById(senderId)) {
            throw new ResourceNotFoundException("Sender not found with id: " + senderId);
        }

        messageRepository.markMessagesAsRead(userId, senderId);

        // Send WebSocket notification about read status
        try {
            messagingTemplate.convertAndSendToUser(
                    senderId.toString(),
                    "/queue/message-read",
                    userId
            );
        } catch (Exception e) {
            log.error("Failed to send read notification via WebSocket: {}", e.getMessage());
        }
    }

    @Override
    public void markMessageAsRead(Long messageId) {
        log.info("Marking message as read: {}", messageId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));

        if (!message.getIsRead()) {
            message.setIsRead(true);
            messageRepository.save(message);

            // Send WebSocket notification
            try {
                messagingTemplate.convertAndSendToUser(
                        message.getSenderId().toString(),
                        "/queue/message-read",
                        messageId
                );
            } catch (Exception e) {
                log.error("Failed to send read notification via WebSocket: {}", e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadMessagesCount(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return messageRepository.countUnreadMessagesByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getMessagesByContract(Long contractId) {
        log.info("Getting messages for contract: {}", contractId);

        List<Message> messages = messageRepository.findMessagesByContractId(contractId);
        return messages.stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getMessagesBySpace(Long spaceId) {
        log.info("Getting messages for space: {}", spaceId);

        List<Message> messages = messageRepository.findMessagesBySpaceId(spaceId);
        return messages.stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMessage(Long messageId, Long userId) {
        log.info("Deleting message {} by user {}", messageId, userId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));

        // Only sender can delete their own messages
        if (!message.getSenderId().equals(userId)) {
            throw new InsufficientPermissionsException("You can only delete your own messages");
        }
    }
}