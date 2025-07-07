package club.memoni.backend

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class BackendApplicationTests {
    @Test
    fun contextLoads() {
        // 스프링 컨텍스트 로딩 테스트
    }
}
