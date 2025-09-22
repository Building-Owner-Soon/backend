package com.bos.backend.application.mapper

import com.bos.backend.domain.notification.entity.Notification
import com.bos.backend.presentation.notification.dto.NotificationResponseDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper(componentModel = "spring")
interface NotificationMapper {
    @Mapping(source = "read", target = "isRead")
    fun toNotificationResponseDTO(notification: Notification): NotificationResponseDTO

    companion object {
        val INSTANCE: NotificationMapper = Mappers.getMapper(NotificationMapper::class.java)
    }
}
