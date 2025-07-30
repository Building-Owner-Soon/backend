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
    /**
     * R2DBC Custom Conversions Configuration
     *
     * Unlike JPA's @AttributeConverter, R2DBC does not have built-in AttributeConverter support.
     * Custom type conversions are handled through R2dbcCustomConversions and Converter implementations.
     *
     * This configuration provides custom converters for:
     * - Instant <-> LocalDateTime conversion for database compatibility
     *
     * For enum type conversions (like ProviderType), manual string conversion is used
     * in entity classes through getter/setter patterns rather than automatic conversion.
     *
     * R2DBC operates differently from JPA:
     * 1. No automatic entity lifecycle management
     * 2. No lazy loading
     * 3. Manual conversion for complex types
     * 4. Reactive programming model throughout
     */
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
