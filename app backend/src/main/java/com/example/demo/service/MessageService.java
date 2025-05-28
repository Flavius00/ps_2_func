package com.example.demo.service;

import com.example.demo.dto.MessageDto;
import com.example.demo.model.Message;
import java.util.List;

public interface MessageService {
    // Send a new message
    Message sendMessage(Long senderId, Long recipientId, String content, String messageType, Long relatedContractId, Long relatedSpaceId);

    // Get conversation between two users
    List<MessageDto> getConversation(Long user1Id, Long user2Id);

    // Get all messages for a user
    List<MessageDto> getUserMessages(Long userId);

    // Get unread messages for a user
    List<MessageDto> getUnreadMessages(Long userId);

    // Get recent conversations for a user
    List<MessageDto> getRecentConversations(Long userId);

    // Mark messages as read
    void markMessagesAsRead(Long userId, Long senderId);

    // Mark single message as read
    void markMessageAsRead(Long messageId);

    // Get unread messages count
    long getUnreadMessagesCount(Long userId);

    // Get messages by contract
    List<MessageDto> getMessagesByContract(Long contractId);

    // Get messages by space
    List<MessageDto> getMessagesBySpace(Long spaceId);

    // Delete message (soft delete or hard delete)
    void deleteMessage(Long messageId, Long userId);

    // Check if user can access message
    boolean canUserAccessMessage(Long messageId, Long userId);
}