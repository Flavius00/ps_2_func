package com.example.demo.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageCreateDto {
    private Long recipientId;
    private String content;
    private String messageType;
    private Long relatedContractId;
    private Long relatedSpaceId;
}