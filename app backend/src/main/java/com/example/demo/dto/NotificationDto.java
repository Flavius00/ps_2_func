package com.example.demo.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String title;
    private String message;
    private String type;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private String actionUrl;

    // Recipient information
    private Long recipientId;
    private String recipientName;

    // Related entities
    private Long relatedContractId;
    private Long relatedSpaceId;
    private Long relatedUserId;
}