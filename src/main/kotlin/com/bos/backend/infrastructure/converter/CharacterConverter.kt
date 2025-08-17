package com.bos.backend.infrastructure.converter

import com.bos.backend.domain.user.entity.Character
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@ReadingConverter
class CharacterReadingConverter(
    private val objectMapper: ObjectMapper,
) : Converter<String, Character> {
    override fun convert(source: String): Character = objectMapper.readValue(source, Character::class.java)
}

@WritingConverter
class CharacterWritingConverter(
    private val objectMapper: ObjectMapper,
) : Converter<Character, String> {
    override fun convert(source: Character): String = objectMapper.writeValueAsString(source)
}
