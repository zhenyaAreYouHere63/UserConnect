package org.task.authenticify.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.task.authenticify.dto.auth.TokenPair;
import org.task.authenticify.dto.user.UserDto;
import org.task.authenticify.entity.user.Role;
import org.task.authenticify.service.JwtService;
import org.task.authenticify.service.UserService;
import java.util.UUID;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    private UserDto userDto;

    private TokenPair tokenPair;

    @Test
    void registration_shouldRegisterUser() throws Exception {
        UUID uuid = UUID.randomUUID();
        userDto = new UserDto(uuid, "email", "password");

        TokenPair tokenPair = new TokenPair("accessToken", "refreshToken");

        when(userService.registerUser(userDto)).thenReturn(tokenPair);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value(tokenPair.getAccessToken()))
                .andExpect(jsonPath("$.refreshToken").value(tokenPair.getRefreshToken()));
    }

    @Test
    void registration_shouldReturnBadRequest400WhenEmailBlank() throws Exception {
        UUID uuid = UUID.randomUUID();
        userDto = new UserDto(uuid, "", "password");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registration_shouldReturnBadRequest400WhenFieldsEmailAndPasswordBlank() throws Exception {
        UUID uuid = UUID.randomUUID();
        userDto = new UserDto(uuid, "", "");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }
}
