package com.bos.backend.application.mapper

import com.bos.backend.domain.user.entity.User
import com.bos.backend.presentation.user.dto.UserProfileDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper(componentModel = "spring")
interface UserMapper {
    @Mapping(source = "notificationAllowed", target = "isNotificationAllowed")
    @Mapping(source = "marketingAgreed", target = "isMarketingAgreed")
    fun toUserProfileDTO(user: User): UserProfileDTO

    companion object {
        val INSTANCE: UserMapper = Mappers.getMapper(UserMapper::class.java)
    }
}
