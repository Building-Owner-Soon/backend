package com.bos.backend.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * spring boot의 자동 설정을 사용할 경우 기본 직렬화 방식 사용 (JdkSerializationRedisSerializer)
 * 데이터가 바이트 배열로 저장되어 Redis에서 읽기 어려렵기 때문에 문자열로 변환하여 저장하고 읽기 쉽게 설정
 */

@Configuration
class RedisConfiguration {
// spring이 이 클래서를 설정 클래스로 인식하고 Bean들을 등록
    // redisTemplate을 spring bean으로 등록하여 다른 클래스에서 @Autowired로 주입받아 사용 가능
    // RedisConnectionFactory를 주입받아 RedisTemplate을 생성하고 설정: reids 서버와의 연결을 관리하는 팩토리로 redis 서버 주소, 포트 , 비밀번호 등 연결 정보 제공
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.connectionFactory = connectionFactory

        // Redis에 저장될 때 바이트 배열로 변환되어 읽기 어려워서 직렬화를 설정
        // 키, 값, 해시키, 해사 값을 string으로 반환
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = StringRedisSerializer()

        template.afterPropertiesSet()
        return template
    }
}
