package com._1.spring_rest_api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String TEST_SECRET_KEY = "thisIsATestSecretKeyForJwtServiceTestingPurposesOnly";
    private final String TEST_EMAIL = "test@example.com";
    private UserDetails userDetails;
    private String token;

    @BeforeEach
    void setUp() {
        // 직접 JwtService 인스턴스 생성
        jwtService = new JwtService();

        // Reflection으로 secretKey 설정
        setSecretKey(jwtService, TEST_SECRET_KEY);

        // 테스트용 사용자 생성
        userDetails = new User(TEST_EMAIL, "", new ArrayList<>());

        // 토큰 생성
        token = jwtService.generateToken(userDetails);
    }

    // JwtService의 secretKey 필드에 테스트 값을 설정하는 헬퍼 메서드
    private void setSecretKey(JwtService service, String secretKey) {
        try {
            java.lang.reflect.Field field = JwtService.class.getDeclaredField("secretKey");
            field.setAccessible(true);
            field.set(service, secretKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set secret key for testing", e);
        }
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        // When
        String extractedUsername = jwtService.extractUsername(token);

        // Then
        assertEquals(TEST_EMAIL, extractedUsername);
    }

    @Test
    void extractUsername_shouldReturnNullForInvalidToken() {
        // Given
        String invalidToken = "invalid.token.string";

        // When
        String username = jwtService.extractUsername(invalidToken);

        // Then
        assertNull(username);
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        // When & Then
        assertNotNull(token);
        assertTrue(token.length() > 0);

        // Verify token contents
        String username = jwtService.extractUsername(token);
        assertEquals(TEST_EMAIL, username);
    }

    @Test
    void generateToken_shouldCreateTokenWithExtraClaims() {
        // Given
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");
        extraClaims.put("userId", 123);

        // When
        String tokenWithClaims = jwtService.generateToken(userDetails, extraClaims);

        // Then
        assertNotNull(tokenWithClaims);
        assertEquals("ADMIN", jwtService.extractClaim(tokenWithClaims, claims -> claims.get("role", String.class)));
        assertEquals(123, (Integer) jwtService.extractClaim(tokenWithClaims, claims -> claims.get("userId", Integer.class)));
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        // When
        boolean isValid = jwtService.validateToken(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_shouldReturnFalseForDifferentUser() {
        // Given
        UserDetails differentUser = new User("different@example.com", "", new ArrayList<>());

        // When
        boolean isValid = jwtService.validateToken(token, differentUser);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalseForExpiredToken() {
        // Given
        String expiredToken = createExpiredToken();

        // When
        boolean isValid = jwtService.validateToken(expiredToken, userDetails);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidSignature() {
        // Given
        String invalidSignatureToken = createTokenWithDifferentSignature();

        // When
        boolean isValid = jwtService.validateToken(invalidSignatureToken, userDetails);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalseForMalformedToken() {
        // Given
        String malformedToken = "malformed.token";

        // When
        boolean isValid = jwtService.validateToken(malformedToken, userDetails);

        // Then
        assertFalse(isValid);
    }

    @Test
    void extractExpiration_shouldReturnCorrectExpirationDate() {
        // When
        Date expirationDate = jwtService.extractExpiration(token);

        // Then
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void extractExpiration_shouldReturnNullForInvalidToken() {
        // Given
        String invalidToken = "invalid.token";

        // When
        Date expirationDate = jwtService.extractExpiration(invalidToken);

        // Then
        assertNull(expirationDate);
    }

    // 헬퍼 메서드들

    private String createExpiredToken() {
        return Jwts.builder()
                .setSubject(TEST_EMAIL)
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000)) // 2초 전
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // 1초 전 (만료됨)
                .signWith(Keys.hmacShaKeyFor(TEST_SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    private String createTokenWithDifferentSignature() {
        String differentKey = "aDifferentSecretKeyForTestingInvalidSignatures";
        return Jwts.builder()
                .setSubject(TEST_EMAIL)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(Keys.hmacShaKeyFor(differentKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
}