package com._1.spring_rest_api.repository;

import com._1.spring_rest_api.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
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
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {
        // Given
        String email = "test@example.com";

        User user = User.builder()
                .email(email)
                .name("Test User")
                .isActive(true)
                .build();

        entityManager.persist(user);
        entityManager.flush();

        // When
        Optional<User> found = userRepository.findByEmail(email);

        // Then
        assertTrue(found.isPresent());
        assertEquals(email, found.get().getEmail());
        assertEquals("Test User", found.get().getName());
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailNotExists() {
        // Given
        String nonExistentEmail = "nonexistent@example.com";

        // When
        Optional<User> found = userRepository.findByEmail(nonExistentEmail);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void save_shouldPersistUser() {
        // Given
        User user = User.builder()
                .email("save@example.com")
                .name("Save Test User")
                .isActive(true)
                .build();

        // When
        User saved = userRepository.save(user);

        // Then
        assertNotNull(saved.getId());

        // Verify user is persisted
        User found = entityManager.find(User.class, saved.getId());
        assertNotNull(found);
        assertEquals("save@example.com", found.getEmail());
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        // Given
        User user1 = User.builder()
                .email("user1@example.com")
                .name("User One")
                .isActive(true)
                .build();
        entityManager.persist(user1);

        User user2 = User.builder()
                .email("user2@example.com")
                .name("User Two")
                .isActive(true)
                .build();
        entityManager.persist(user2);

        entityManager.flush();

        // When
        List<User> users = userRepository.findAll();

        // Then
        assertFalse(users.isEmpty());
        assertTrue(users.size() >= 2);
        assertTrue(users.stream().anyMatch(u -> "user1@example.com".equals(u.getEmail())));
        assertTrue(users.stream().anyMatch(u -> "user2@example.com".equals(u.getEmail())));
    }

    @Test
    void update_shouldModifyUser() {
        // Given
        User user = User.builder()
                .email("update@example.com")
                .name("Original Name")
                .isActive(true)
                .build();

        entityManager.persist(user);
        entityManager.flush();

        // When - update name
        User savedUser = entityManager.find(User.class, user.getId());
        // 도메인 로직을 사용한 방식으로 변경
        savedUser.updateName("Updated Name");
        userRepository.save(savedUser);

        // Then
        User updatedUser = entityManager.find(User.class, user.getId());
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("update@example.com", updatedUser.getEmail());
    }

    @Test
    void delete_shouldRemoveUser() {
        // Given
        User user = User.builder()
                .email("delete@example.com")
                .name("Delete Test User")
                .isActive(true)
                .build();

        entityManager.persist(user);
        entityManager.flush();

        // When
        userRepository.delete(user);

        // Then
        User deletedUser = entityManager.find(User.class, user.getId());
        assertNull(deletedUser);
    }

    @Test
    void findById_shouldReturnUser_whenExists() {
        // Given
        User user = User.builder()
                .email("findbyid@example.com")
                .name("Find By ID User")
                .isActive(true)
                .build();

        entityManager.persist(user);
        entityManager.flush();

        // When
        Optional<User> found = userRepository.findById(user.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(user.getId(), found.get().getId());
        assertEquals("findbyid@example.com", found.get().getEmail());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        // Given
        Long nonExistentId = 999999L;

        // When
        Optional<User> found = userRepository.findById(nonExistentId);

        // Then
        assertFalse(found.isPresent());
    }
}