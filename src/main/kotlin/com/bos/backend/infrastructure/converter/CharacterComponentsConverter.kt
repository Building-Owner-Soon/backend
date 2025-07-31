package com.bos.backend.infrastructure.converter

import com.bos.backend.domain.user.entity.CharacterComponents
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@ReadingConverter
class CharacterComponentsReadingConverter(
    private val objectMapper: ObjectMapper,
) : Converter<String, CharacterComponents> {
    override fun convert(source: String): CharacterComponents =
        objectMapper.readValue(source, CharacterComponents::class.java)
}

@WritingConverter
class CharacterComponentsWritingConverter(
    private val objectMapper: ObjectMapper,
) : Converter<CharacterComponents, String> {
    override fun convert(source: CharacterComponents): String = objectMapper.writeValueAsString(source)
}
