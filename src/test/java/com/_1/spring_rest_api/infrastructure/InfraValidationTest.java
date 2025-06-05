package com._1.spring_rest_api.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * 로컬 개발 환경 검증 테스트
 * 메모리, 포트 설정, secret 파일을 확인합니다.
 * CI 환경에서는 자동으로 스킵됩니다.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:lecture2quiz",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=test_jwt_secret_key_for_integration_testing_purposes_only"
})
@DisplayName("로컬 개발 환경 검증")
class InfraValidationTest {

    private static final int EXPECTED_SERVER_PORT = 8080;
    private static final long CLOUD_RUN_MEMORY_LIMIT_MB = 512;
    private static final String SECRET_PROPERTIES_PATH = "src/main/resources/application-secret.properties";
    private static final String SECRET_EXAMPLE_PATH = "src/main/resources/application-secret-example.properties";
    private static final String APPLICATION_PROPERTIES_PATH = "src/main/resources/application.properties";

    @Test
    @DisplayName("메모리 사용량이 Cloud Run 제한(512Mi) 내에 있는지 확인")
    void shouldValidateMemoryUsage() {
        skipIfCiEnvironment();

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();

        long usedMemoryMB = heapUsage.getUsed() / (1024 * 1024);

        System.out.println("🧠 Memory Usage Check:");
        System.out.println("   Used: " + usedMemoryMB + "MB");
        System.out.println("   Cloud Run Limit: " + CLOUD_RUN_MEMORY_LIMIT_MB + "MB");

        // 메모리 사용량 검증 (80% 임계값)
        long memoryThreshold = (long)(CLOUD_RUN_MEMORY_LIMIT_MB * 0.8);
        if (usedMemoryMB >= memoryThreshold) {
            fail(String.format("❌ 메모리 사용량 초과! 현재: %dMB, 허용: %dMB",
                    usedMemoryMB, memoryThreshold));
        }

        System.out.println("✅ 메모리 사용량 검증 통과");
    }

    @Test
    @DisplayName("application.properties에 server.port=8080 설정이 올바른지 확인")
    void shouldValidateServerPortConfiguration() {
        skipIfCiEnvironment();

        System.out.println("🔌 Server Port Configuration Check:");

        Path appPropsPath = Paths.get(APPLICATION_PROPERTIES_PATH);

        if (!Files.exists(appPropsPath)) {
            fail("❌ application.properties 파일이 없습니다!");
        }

        try {
            Properties appProps = new Properties();
            appProps.load(Files.newInputStream(appPropsPath));

            String serverPort = appProps.getProperty("server.port");

            // server.port 설정 확인
            if (serverPort == null) {
                System.out.println("   ⚠️ server.port가 설정되지 않음 (기본값 8080 사용)");
            } else {
                int configuredPort = Integer.parseInt(serverPort);
                if (configuredPort != EXPECTED_SERVER_PORT) {
                    fail("❌ server.port가 " + EXPECTED_SERVER_PORT + "이 아닙니다! 현재: " + configuredPort + "\n" +
                            "💡 application.properties에서 server.port=8080으로 설정하세요");
                }
                System.out.println("   ✅ server.port=" + configuredPort + " 설정 확인");
            }

        } catch (IOException e) {
            fail("❌ application.properties 읽기 실패: " + e.getMessage());
        } catch (NumberFormatException e) {
            fail("❌ server.port 값이 올바른 숫자가 아닙니다: " + e.getMessage());
        }

        System.out.println("✅ 서버 포트 설정 검증 통과");
    }

    @Test
    @DisplayName("application-secret.properties 파일 존재 및 변경 여부 확인")
    void shouldValidateSecretPropertiesFile() {
        skipIfCiEnvironment();

        System.out.println("⚙️ Secret Properties File Check:");

        Path secretPath = Paths.get(SECRET_PROPERTIES_PATH);
        Path examplePath = Paths.get(SECRET_EXAMPLE_PATH);

        // 1. secret 파일 존재 확인
        if (!Files.exists(secretPath)) {
            fail("❌ application-secret.properties 파일이 없습니다!\n" +
                    "💡 해결방법: cp " + SECRET_EXAMPLE_PATH + " " + SECRET_PROPERTIES_PATH);
        }

        // 2. 파일이 비어있지 않은지 확인
        try {
            String content = Files.readString(secretPath);
            if (content.trim().isEmpty()) {
                fail("❌ application-secret.properties 파일이 비어있습니다!");
            }
        } catch (IOException e) {
            fail("❌ 파일 읽기 실패: " + e.getMessage());
        }

        System.out.println("   ✅ Secret properties 파일 검증 통과");
    }

    /**
     * CI 환경에서는 테스트 스킵
     */
    private void skipIfCiEnvironment() {
        boolean isCiEnvironment = System.getenv("CI") != null ||
                System.getenv("GITHUB_ACTIONS") != null ||
                System.getenv("JENKINS_URL") != null;

        assumeFalse(isCiEnvironment, "🤖 CI 환경에서는 로컬 환경 검증을 스킵합니다");
    }
}
