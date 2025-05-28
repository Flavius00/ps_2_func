package com.example.demo.mapper;

import com.example.demo.dto.NotificationDto;
import com.example.demo.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDto toDto(Notification entity) {
        if (entity == null) {
            return null;
        }

        return NotificationDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .type(entity.getType() != null ? entity.getType().name() : null)
                .isRead(entity.getIsRead())
                .createdAt(entity.getCreatedAt())
                .actionUrl(entity.getActionUrl())
                .recipientId(entity.getRecipientId())
                .recipientName(entity.getRecipientName())
                .relatedContractId(entity.getRelatedContractId())
                .relatedSpaceId(entity.getRelatedSpaceId())
                .relatedUserId(entity.getRelatedUserId())
                .build();
    }
}