package com.example.demo.repository;

import com.example.demo.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Find messages between two users
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :user1Id AND m.recipient.id = :user2Id) OR " +
            "(m.sender.id = :user2Id AND m.recipient.id = :user1Id) " +
            "ORDER BY m.sentAt ASC")
    List<Message> findConversationBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    // Find all messages for a user (sent or received)
    @Query("SELECT m FROM Message m WHERE m.sender.id = :userId OR m.recipient.id = :userId ORDER BY m.sentAt DESC")
    List<Message> findMessagesByUserId(@Param("userId") Long userId);

    // Find unread messages for a user
    @Query("SELECT m FROM Message m WHERE m.recipient.id = :userId AND m.isRead = false ORDER BY m.sentAt DESC")
    List<Message> findUnreadMessagesByUserId(@Param("userId") Long userId);

    // Find recent conversations for a user
    @Query("SELECT DISTINCT m FROM Message m WHERE " +
            "(m.sender.id = :userId OR m.recipient.id = :userId) AND " +
            "m.sentAt = (SELECT MAX(m2.sentAt) FROM Message m2 WHERE " +
            "((m2.sender.id = m.sender.id AND m2.recipient.id = m.recipient.id) OR " +
            "(m2.sender.id = m.recipient.id AND m2.recipient.id = m.sender.id))) " +
            "ORDER BY m.sentAt DESC")
    List<Message> findRecentConversations(@Param("userId") Long userId);

    // Count unread messages for a user
    @Query("SELECT COUNT(m) FROM Message m WHERE m.recipient.id = :userId AND m.isRead = false")
    long countUnreadMessagesByUserId(@Param("userId") Long userId);

    // Find messages by contract
    @Query("SELECT m FROM Message m WHERE m.relatedContractId = :contractId ORDER BY m.sentAt ASC")
    List<Message> findMessagesByContractId(@Param("contractId") Long contractId);

    // Find messages by space
    @Query("SELECT m FROM Message m WHERE m.relatedSpaceId = :spaceId ORDER BY m.sentAt ASC")
    List<Message> findMessagesBySpaceId(@Param("spaceId") Long spaceId);

    // Find messages sent after a specific time
    @Query("SELECT m FROM Message m WHERE m.sentAt > :since ORDER BY m.sentAt ASC")
    List<Message> findMessagesSince(@Param("since") LocalDateTime since);

    // Mark messages as read
    @Query("UPDATE Message m SET m.isRead = true WHERE m.recipient.id = :userId AND m.sender.id = :senderId AND m.isRead = false")
    void markMessagesAsRead(@Param("userId") Long userId, @Param("senderId") Long senderId);
}