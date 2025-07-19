package com.bos.backend.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.CustomConversions
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.DialectResolver
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.core.DatabaseClient
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Configuration
@EnableR2dbcRepositories(basePackages = ["com.bos.backend.infrastructure.persistence"])
class R2dbcConfiguration {
    // TODO: JPA AttributeConverter와 유사하게 R2DBC에서도 AttributeConverter를 지원하는지 확인 필요
    // TODO: 동작 원리 디깅
    @Bean
    fun r2dbcCustomConversions(databaseClient: DatabaseClient): R2dbcCustomConversions {
        val dialect = DialectResolver.getDialect(databaseClient.connectionFactory)
        val converters = dialect.converters.toMutableList()
        converters.addAll(R2dbcCustomConversions.STORE_CONVERTERS)
        return R2dbcCustomConversions(
            CustomConversions.StoreConversions.of(dialect.getSimpleTypeHolder(), converters),
            listOf(
                InstantToLocalDateTimeConverter(),
                LocalDateTimeToInstantConverter(),
            ),
        )
    }
}

@WritingConverter
class InstantToLocalDateTimeConverter : Converter<Instant, LocalDateTime> {
    override fun convert(source: Instant): LocalDateTime = LocalDateTime.ofInstant(source, ZoneOffset.UTC)
}

@ReadingConverter
class LocalDateTimeToInstantConverter : Converter<LocalDateTime, Instant> {
    override fun convert(source: LocalDateTime): Instant = source.toInstant(ZoneOffset.UTC)
}
