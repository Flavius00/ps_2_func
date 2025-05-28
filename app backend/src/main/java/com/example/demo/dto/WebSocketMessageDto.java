package com.example.demo.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessageDto {
    private String type; // "NEW_MESSAGE", "MESSAGE_READ", etc.
    private MessageDto message;
    private Long userId; // Target user for the message
}
