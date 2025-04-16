package com._1.spring_rest_api.security;

import com._1.spring_rest_api.entity.User;
import com._1.spring_rest_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        // Given
        String email = "test@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setName("Test User");
        user.setIsActive(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
        assertEquals("", userDetails.getPassword()); // OAuth2 users don't have a password

        // Verify user has ROLE_USER authority
        boolean hasUserRole = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_USER"));
        assertTrue(hasUserRole);

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(email)
        );

        assertTrue(exception.getMessage().contains(email));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void loadUserByUsername_shouldReturnDisabledUser_whenUserIsInactive() {
        // Given
        String email = "inactive@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setName("Inactive User");
        user.setIsActive(false); // User is inactive

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertFalse(userDetails.isEnabled()); // User should be disabled

        verify(userRepository, times(1)).findByEmail(email);
    }
}