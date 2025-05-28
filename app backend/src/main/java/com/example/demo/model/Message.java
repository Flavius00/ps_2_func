package com.example.demo.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "messages")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @JsonIgnore
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    @JsonIgnore
    private User recipient;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MessageType messageType = MessageType.TEXT;

    // Optional: Reference to related entities (contract, space, etc.)
    private Long relatedContractId;
    private Long relatedSpaceId;

    // JSON helper methods for sender info
    public Long getSenderId() {
        return sender != null ? sender.getId() : null;
    }

    public String getSenderName() {
        return sender != null ? sender.getName() : null;
    }

    public String getSenderRole() {
        return sender != null && sender.getRole() != null ? sender.getRole().name() : null;
    }

    // JSON helper methods for recipient info
    public Long getRecipientId() {
        return recipient != null ? recipient.getId() : null;
    }

    public String getRecipientName() {
        return recipient != null ? recipient.getName() : null;
    }

    public String getRecipientRole() {
        return recipient != null && recipient.getRole() != null ? recipient.getRole().name() : null;
    }

    public enum MessageType {
        TEXT,
        CONTRACT_INQUIRY,
        SPACE_INQUIRY,
        SYSTEM_MESSAGE
    }
}