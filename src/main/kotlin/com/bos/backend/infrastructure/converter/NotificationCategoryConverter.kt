package com.bos.backend.infrastructure.converter

import com.bos.backend.domain.notification.enums.NotificationCategory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@ReadingConverter
class NotificationCategoryReadConverter : Converter<String, NotificationCategory> {
    override fun convert(source: String): NotificationCategory {
        return NotificationCategory.valueOf(source)
    }
}

@WritingConverter
class NotificationCategoryWriteConverter : Converter<NotificationCategory, String> {
    override fun convert(source: NotificationCategory): String {
        return source.name
    }
}
