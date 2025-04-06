package com._1.spring_rest_api.repository;

import com._1.spring_rest_api.entity.User;
import com._1.spring_rest_api.entity.UserKakao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:lecture2quiz",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UserKakaoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserKakaoRepository userKakaoRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByKakaoAccountId_shouldReturnUserKakao_whenExists() {
        // Given
        String kakaoAccountId = "kakao_123456789";

        // Create a user first
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setIsActive(true);
        entityManager.persist(user);

        // Create and link UserKakao
        UserKakao userKakao = new UserKakao();
        userKakao.setUser(user);
        userKakao.setKakaoAccountId(kakaoAccountId);
        userKakao.setAccessToken("test_access_token");
        userKakao.setRefreshToken("test_refresh_token");
        userKakao.setTokenExpiresAt(LocalDateTime.now().plusHours(1));

        entityManager.persist(userKakao);
        entityManager.flush();

        // When
        Optional<UserKakao> found = userKakaoRepository.findByKakaoAccountId(kakaoAccountId);

        // Then
        assertTrue(found.isPresent());
        assertEquals(kakaoAccountId, found.get().getKakaoAccountId());
        assertEquals(user.getId(), found.get().getUser().getId());
        assertEquals("test_access_token", found.get().getAccessToken());
    }

    @Test
    void findByKakaoAccountId_shouldReturnEmpty_whenNotExists() {
        // Given
        String nonExistentKakaoId = "non_existent_kakao_id";

        // When
        Optional<UserKakao> found = userKakaoRepository.findByKakaoAccountId(nonExistentKakaoId);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void save_shouldPersistUserKakao() {
        // Given
        User user = new User();
        user.setEmail("save_test@example.com");
        user.setName("Save Test User");
        user.setIsActive(true);
        entityManager.persist(user);

        UserKakao userKakao = new UserKakao();
        userKakao.setUser(user);
        userKakao.setKakaoAccountId("kakao_save_test_id");
        userKakao.setAccessToken("save_test_access_token");
        userKakao.setRefreshToken("save_test_refresh_token");
        userKakao.setTokenExpiresAt(LocalDateTime.now().plusHours(1));

        // When
        UserKakao saved = userKakaoRepository.save(userKakao);

        // Then
        assertNotNull(saved.getId());

        // Verify data can be retrieved
        Optional<UserKakao> found = userKakaoRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("kakao_save_test_id", found.get().getKakaoAccountId());
    }

    @Test
    void delete_shouldRemoveUserKakao() {
        // Given
        User user = new User();
        user.setEmail("delete_test@example.com");
        user.setName("Delete Test User");
        user.setIsActive(true);
        entityManager.persist(user);

        UserKakao userKakao = new UserKakao();
        userKakao.setUser(user);
        userKakao.setKakaoAccountId("kakao_delete_test_id");
        userKakao.setAccessToken("delete_test_access_token");
        userKakao.setRefreshToken("delete_test_refresh_token");
        userKakao.setTokenExpiresAt(LocalDateTime.now().plusHours(1));

        entityManager.persist(userKakao);
        entityManager.flush();

        // When
        userKakaoRepository.delete(userKakao);

        // Then
        Optional<UserKakao> found = userKakaoRepository.findById(userKakao.getId());
        assertFalse(found.isPresent());
    }
}