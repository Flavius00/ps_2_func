package com.example.demo.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "notifications")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    @JsonIgnore
    private User recipient;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Reference to related entities
    private Long relatedContractId;
    private Long relatedSpaceId;
    private Long relatedUserId;

    @Column(length = 500)
    private String actionUrl; // URL for frontend navigation

    // JSON helper methods
    public Long getRecipientId() {
        return recipient != null ? recipient.getId() : null;
    }

    public String getRecipientName() {
        return recipient != null ? recipient.getName() : null;
    }

    public enum NotificationType {
        CONTRACT_CREATED,
        CONTRACT_EXPIRING,
        CONTRACT_TERMINATED,
        SPACE_AVAILABLE,
        MESSAGE_RECEIVED,
        PAYMENT_DUE,
        SYSTEM_ALERT
    }
}