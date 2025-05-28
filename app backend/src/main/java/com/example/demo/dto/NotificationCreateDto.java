package com.example.demo.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreateDto {
    private Long recipientId;
    private String title;
    private String message;
    private String type;
    private String actionUrl;
    private Long relatedContractId;
    private Long relatedSpaceId;
    private Long relatedUserId;
}