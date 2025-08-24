package com.bos.backend.infrastructure.database

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.flywaydb.core.Flyway
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.DriverManager

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class FlywayMigrationIntegrationTest :
    StringSpec({

        "Gradle Flyway 태스크로 마이그레이션이 정상적으로 실행되어야 한다" {
            // Given
            val processBuilder = ProcessBuilder()
            processBuilder.environment()["jdbc-url"] = mariadb.jdbcUrl
            processBuilder.environment()["db-user"] = mariadb.username
            processBuilder.environment()["db-password"] = mariadb.password

            // When
            val process =
                processBuilder
                    .command("./gradlew", "flywayMigrate")
                    .directory(java.io.File("."))
                    .start()

            val exitCode = process.waitFor()
            val output = process.inputStream.bufferedReader().readText()

            // Then
            exitCode shouldBe 0
            output.contains("SUCCESS") || output.contains("Build Successful") shouldBe true
        }

        "Gradle Flyway Info 태스크로 마이그레이션 상태를 확인할 수 있어야 한다" {
            // Given
            // 먼저 마이그레이션 실행
            val migrateProcess = ProcessBuilder()
            migrateProcess.environment()["jdbc-url"] = mariadb.jdbcUrl
            migrateProcess.environment()["db-user"] = mariadb.username
            migrateProcess.environment()["db-password"] = mariadb.password
            migrateProcess
                .command("./gradlew", "flywayMigrate")
                .directory(java.io.File("."))
                .start()
                .waitFor()

            // When
            val infoProcess = ProcessBuilder()
            infoProcess.environment()["jdbc-url"] = mariadb.jdbcUrl
            infoProcess.environment()["db-user"] = mariadb.username
            infoProcess.environment()["db-password"] = mariadb.password

            val process =
                infoProcess
                    .command("./gradlew", "flywayInfo")
                    .directory(java.io.File("."))
                    .start()

            val exitCode = process.waitFor()
            val output = process.inputStream.bufferedReader().readText()

            // Then
            exitCode shouldBe 0
            output.contains("SUCCESS") || output.contains("Build Successful") shouldBe true
        }

        "Gradle 태스크 실행 후 모든 테이블이 생성되어야 한다" {
            // Given - Gradle 태스크로 마이그레이션 실행
            val processBuilder = ProcessBuilder()
            processBuilder.environment()["jdbc-url"] = mariadb.jdbcUrl
            processBuilder.environment()["db-user"] = mariadb.username
            processBuilder.environment()["db-password"] = mariadb.password

            val process =
                processBuilder
                    .command("./gradlew", "flywayMigrate")
                    .directory(java.io.File("."))
                    .start()
            process.waitFor()

            // When - 테이블 존재 여부 확인
            val connection = DriverManager.getConnection(mariadb.jdbcUrl, mariadb.username, mariadb.password)
            val statement = connection.createStatement()

            val tablesResult = statement.executeQuery("SHOW TABLES")
            val tables = mutableSetOf<String>()

            while (tablesResult.next()) {
                tables.add(tablesResult.getString(1))
            }

            // Then
            tables shouldNotBe emptySet<String>()
            tables.contains("flyway_schema_history") shouldBe true
            // V2 마이그레이션에서 생성된 테이블 확인
            tables.contains("flyway_test") shouldBe true

            connection.close()
        }

        "users 테이블의 스키마가 R2DBC 엔티티와 일치해야 한다" {
            // Given
            val flyway =
                Flyway
                    .configure()
                    .dataSource(mariadb.jdbcUrl, mariadb.username, mariadb.password)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .baselineVersion("1")
                    .load()

            flyway.migrate()

            // When
            val connection = DriverManager.getConnection(mariadb.jdbcUrl, mariadb.username, mariadb.password)
            val statement = connection.createStatement()

            // users 테이블이 이미 존재하는지 확인 (기존 데이터베이스에서)
            val columnsResult =
                statement.executeQuery(
                    """
            SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users'
            ORDER BY ORDINAL_POSITION
        """,
                )

            val columns = mutableMapOf<String, String>()
            while (columnsResult.next()) {
                columns[columnsResult.getString("COLUMN_NAME")] = columnsResult.getString("DATA_TYPE")
            }

            // Then
            // User 엔티티의 필드와 매핑되는 컬럼들이 존재해야 함
            columns.containsKey("id") shouldBe true
            columns.containsKey("nickname") shouldBe true
            columns.containsKey("notification_allowed") shouldBe true
            columns.containsKey("marketing_agreed") shouldBe true
            columns.containsKey("character_components") shouldBe true
            columns.containsKey("home_type") shouldBe true
            columns.containsKey("created_at") shouldBe true
            columns.containsKey("updated_at") shouldBe true
            columns.containsKey("deleted_at") shouldBe true

            // 데이터 타입 검증
            columns["id"] shouldBe "bigint"
            columns["nickname"] shouldBe "varchar"
            columns["character_components"] shouldBe "json"

            connection.close()
        }

        "Gradle 태스크 실행 후 마이그레이션 히스토리가 올바르게 기록되어야 한다" {
            // Given - Gradle 태스크로 마이그레이션 실행
            val processBuilder = ProcessBuilder()
            processBuilder.environment()["jdbc-url"] = mariadb.jdbcUrl
            processBuilder.environment()["db-user"] = mariadb.username
            processBuilder.environment()["db-password"] = mariadb.password

            val process =
                processBuilder
                    .command("./gradlew", "flywayMigrate")
                    .directory(java.io.File("."))
                    .start()
            process.waitFor()

            // When - 마이그레이션 히스토리 조회
            val connection = DriverManager.getConnection(mariadb.jdbcUrl, mariadb.username, mariadb.password)
            val statement = connection.createStatement()

            val historyResult =
                statement.executeQuery(
                    """
            SELECT version, description, type, success
            FROM flyway_schema_history
            ORDER BY installed_rank
        """,
                )

            val migrations = mutableListOf<Map<String, Any>>()
            while (historyResult.next()) {
                migrations.add(
                    mapOf(
                        "version" to historyResult.getString("version"),
                        "description" to historyResult.getString("description"),
                        "type" to historyResult.getString("type"),
                        "success" to historyResult.getBoolean("success"),
                    ),
                )
            }

            // Then
            migrations.size shouldBe 2 // baseline + V2

            // Baseline 기록 확인
            val baseline = migrations[0]
            baseline["version"] shouldBe "1"
            baseline["type"] shouldBe "BASELINE"
            baseline["success"] shouldBe true

            // V2 마이그레이션 기록 확인
            val v2Migration = migrations[1]
            v2Migration["version"] shouldBe "2"
            v2Migration["description"] shouldBe "Add test table"
            v2Migration["type"] shouldBe "SQL"
            v2Migration["success"] shouldBe true

            connection.close()
        }
    }) {
    companion object {
        @Container
        @JvmStatic
        val mariadb =
            MariaDBContainer("mariadb:10.11")
                .withDatabaseName("bos_test")
                .withUsername("testuser")
                .withPassword("testpass")

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            // R2DBC 설정 (테스트용)
            registry.add("spring.r2dbc.url") {
                "r2dbc:mariadb://${mariadb.host}:${mariadb.firstMappedPort}/${mariadb.databaseName}"
            }
            registry.add("spring.r2dbc.username") { mariadb.username }
            registry.add("spring.r2dbc.password") { mariadb.password }

            // JDBC 설정 (Flyway용)
            registry.add("spring.datasource.url") { mariadb.jdbcUrl }
            registry.add("spring.datasource.username") { mariadb.username }
            registry.add("spring.datasource.password") { mariadb.password }

            // Flyway 활성화
            registry.add("spring.flyway.enabled") { "true" }
        }
    }
}
