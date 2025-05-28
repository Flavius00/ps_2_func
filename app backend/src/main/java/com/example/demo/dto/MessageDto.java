package com.example.demo.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private String content;
    private LocalDateTime sentAt;
    private Boolean isRead;
    private String messageType;

    // Sender information
    private Long senderId;
    private String senderName;
    private String senderRole;

    // Recipient information
    private Long recipientId;
    private String recipientName;
    private String recipientRole;

    // Related entities
    private Long relatedContractId;
    private Long relatedSpaceId;
}