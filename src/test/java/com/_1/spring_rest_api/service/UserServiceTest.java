package com._1.spring_rest_api.service;

import com._1.spring_rest_api.entity.User;
import com._1.spring_rest_api.entity.UserKakao;
import com._1.spring_rest_api.repository.UserKakaoRepository;
import com._1.spring_rest_api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserKakaoRepository userKakaoRepository;

    private final String KAKAO_ID = "kakao_123456789";
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_NAME = "Test User";

    @AfterEach
    void cleanup() {
        userKakaoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void findByKakaoId_shouldReturnUser_whenExists() {
        // Given - 테스트 데이터 생성
        User user = createTestUser();
        UserKakao userKakao = createTestUserKakao(user);

        // When
        User foundUser = userService.findByKakaoId(KAKAO_ID);

        // Then
        assertNotNull(foundUser);
        assertEquals(TEST_EMAIL, foundUser.getEmail());
        assertEquals(TEST_NAME, foundUser.getName());
        assertTrue(foundUser.getIsActive());

        // UserKakao 정보도 연결되어 있는지 확인
        assertNotNull(foundUser.getUserKakao());
        assertEquals(KAKAO_ID, foundUser.getUserKakao().getKakaoAccountId());
    }

    @Test
    @Transactional
    void findByKakaoId_shouldReturnNull_whenNotExists() {
        // Given - 존재하지 않는 ID 사용
        String nonExistentKakaoId = "non_existent_id";

        // When
        User foundUser = userService.findByKakaoId(nonExistentKakaoId);

        // Then
        assertNull(foundUser);
    }

    @Test
    @Transactional
    void createKakaoUser_shouldCreateAndSaveUserWithKakaoInfo() {
        // When
        User createdUser = userService.createKakaoUser(TEST_EMAIL, TEST_NAME, KAKAO_ID);

        // Then
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals(TEST_EMAIL, createdUser.getEmail());
        assertEquals(TEST_NAME, createdUser.getName());
        assertTrue(createdUser.getIsActive());

        // UserKakao 정보가 생성되었는지 확인
        Optional<UserKakao> userKakao = userKakaoRepository.findByKakaoAccountId(KAKAO_ID);
        assertTrue(userKakao.isPresent());
        assertEquals(createdUser.getId(), userKakao.get().getUser().getId());
    }

    @Test
    @Transactional
    void updateKakaoTokens_shouldUpdateTokensInDatabase() {
        // Given
        User user = createTestUser();
        UserKakao userKakao = createTestUserKakao(user);

        String newAccessToken = "new_access_token";
        String newRefreshToken = "new_refresh_token";
        LocalDateTime newExpiresAt = LocalDateTime.now().plusHours(2);

        // When
        userService.updateKakaoTokens(user, newAccessToken, newRefreshToken, newExpiresAt);

        // Then
        Optional<UserKakao> updatedUserKakao = userKakaoRepository.findById(userKakao.getId());
        assertTrue(updatedUserKakao.isPresent());
        assertEquals(newAccessToken, updatedUserKakao.get().getAccessToken());
        assertEquals(newRefreshToken, updatedUserKakao.get().getRefreshToken());

        // 날짜 비교 - 시간 차이가 1초 이내인지 확인 (정확히 같지 않을 수 있음)
        long timeDiff = Math.abs(newExpiresAt.toEpochSecond(java.time.ZoneOffset.UTC) -
                updatedUserKakao.get().getTokenExpiresAt().toEpochSecond(java.time.ZoneOffset.UTC));
        assertTrue(timeDiff <= 1, "토큰 만료 시간이 예상 범위 내에 있어야 합니다");
    }

    @Test
    @Transactional
    void createUserDetails_shouldReturnCorrectUserDetails() {
        // Given
        User user = createTestUser();

        // When
        UserDetails userDetails = userService.createUserDetails(user);

        // Then
        assertNotNull(userDetails);
        assertEquals(TEST_EMAIL, userDetails.getUsername());
        assertTrue(userDetails.isEnabled());

        // 빈 비밀번호 확인 (OAuth2 사용자는 비밀번호가 없음)
        assertEquals("", userDetails.getPassword());

        // ROLE_USER 권한 확인
        boolean hasUserRole = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_USER"));
        assertTrue(hasUserRole, "사용자는 ROLE_USER 권한을 가져야 합니다");
    }

    // 헬퍼 메서드 - 테스트 사용자 생성
    private User createTestUser() {
        User user = User.builder()
                .email(TEST_EMAIL)
                .name(TEST_NAME)
                .isActive(true)
                .build();
        return userRepository.save(user);
    }

    // 헬퍼 메서드 - 테스트 카카오 계정 정보 생성
    private UserKakao createTestUserKakao(User user) {
        UserKakao userKakao = UserKakao.builder()
                .user(user)
                .kakaoAccountId(KAKAO_ID)
                .accessToken("test_access_token")
                .refreshToken("test_refresh_token")
                .tokenExpiresAt(LocalDateTime.now().plusHours(1))
                .build();
        UserKakao saved = userKakaoRepository.save(userKakao);

        userKakao.linkWithUser(user);

        userRepository.save(user);

        return saved;
    }
}