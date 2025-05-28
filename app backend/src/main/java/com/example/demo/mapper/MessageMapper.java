package com.example.demo.mapper;

import com.example.demo.dto.MessageDto;
import com.example.demo.model.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageDto toDto(Message entity) {
        if (entity == null) {
            return null;
        }

        return MessageDto.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .sentAt(entity.getSentAt())
                .isRead(entity.getIsRead())
                .messageType(entity.getMessageType() != null ? entity.getMessageType().name() : null)
                .senderId(entity.getSenderId())
                .senderName(entity.getSenderName())
                .senderRole(entity.getSenderRole())
                .recipientId(entity.getRecipientId())
                .recipientName(entity.getRecipientName())
                .recipientRole(entity.getRecipientRole())
                .relatedContractId(entity.getRelatedContractId())
                .relatedSpaceId(entity.getRelatedSpaceId())
                .build();
    }
}