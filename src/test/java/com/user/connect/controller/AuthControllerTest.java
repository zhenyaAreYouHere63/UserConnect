package com.user.connect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.user.connect.dto.auth.CredentialsDto;
import com.user.connect.entity.token.TokenPair;
import com.user.connect.service.AuthService;
import com.user.connect.service.JwtService;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    private CredentialsDto credentialsDto;

    private TokenPair tokenPair;

    @BeforeEach
    public void setUpData() {
        credentialsDto = new CredentialsDto("email@test.com", "testPassword");
        tokenPair = new TokenPair("accessToken", "refreshToken");
    }

    @Test
    void login_shouldLoginUser() throws Exception {
        when(authService.login(credentialsDto)).thenReturn(tokenPair);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentialsDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value(tokenPair.getAccessToken()))
                .andExpect(jsonPath("$.refreshToken").value(tokenPair.getRefreshToken()));
    }

    @Test
    void login_shouldReturnBadRequest400WhenEmailBlank() throws Exception {
        credentialsDto = new CredentialsDto("", "password");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentialsDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturnBadRequest400WhenCredentialsDtoBlank() throws Exception {
        credentialsDto = new CredentialsDto("", "");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentialsDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logout_shouldLogoutUser() throws Exception {
        String email = "someUser@gmail.com";

        doNothing().when(authService).logout(email);

        mockMvc.perform(delete("/api/auth/logout/{email}", email))
                .andExpect(status().isOk());

        verify(authService, times(1)).logout(email);
    }

    @Test
    void logout_shouldReturnBadRequest400WhenEmailBlank() throws Exception {
        String email = " ";

        mockMvc.perform(delete("/api/auth/logout/{email}", email))
                .andExpect(status().isBadRequest());

        verify(authService, times(0)).logout(email);
    }
}
