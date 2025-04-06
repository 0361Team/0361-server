package com._1.spring_rest_api.config;

import com._1.spring_rest_api.security.JwtAuthenticationFilter;
import com._1.spring_rest_api.security.OAuth2SuccessHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecurityConfig.class)
@Import(SecurityConfig.class)
@MockBean(JpaMetamodelMappingContext.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void accessHome_shouldBeAllowed() throws Exception {
        // When & Then
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void accessLogin_shouldBeAllowed() throws Exception {
        // When & Then
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void accessOauth2Login_shouldBeAllowed() throws Exception {
        // When & Then
        mockMvc.perform(get("/oauth2/authorization/kakao"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void accessPublicAuth_shouldBeAllowed() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/public/auth"))
                .andExpect(status().isOk());
    }

    @Test
    void accessSwaggerUI_shouldBeAllowed() throws Exception {
        // When & Then
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}