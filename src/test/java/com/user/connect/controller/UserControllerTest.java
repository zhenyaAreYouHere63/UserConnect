package com.user.connect.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.connect.dto.ChangePasswordRequestDto;
import com.user.connect.dto.user.UserDto;
import com.user.connect.entity.token.TokenPair;
import com.user.connect.entity.user.Role;
import com.user.connect.service.JwtService;
import com.user.connect.service.UserService;
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
import org.springframework.test.web.servlet.MvcResult;
import java.util.List;
import java.util.UUID;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

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

    @BeforeEach
    public void setUpData() {
        UUID uuid = UUID.randomUUID();
        userDto = new UserDto(uuid, Role.USER, "email@test.com", "testPassword");
        tokenPair = new TokenPair("accessToken", "refreshToken");
    }

    @Test
    void registration_shouldRegisterUser() throws Exception {
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
        userDto = new UserDto(uuid, Role.USER, "", "password");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registration_shouldReturnBadRequest400WhenFieldsEmailAndPasswordBlank() throws Exception {
        UUID uuid = UUID.randomUUID();
        userDto = new UserDto(uuid, Role.USER, "", "");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resendEmailConfirmation_shouldReturnOk200() throws Exception {
        String email = "someUser@gmail.com";

        mockMvc.perform(get("/api/auth/resend/email-confirmation/{email}", email))
                .andExpect(status().isOk());
    }

    @Test
    void resendEmailConfirmation_shouldReturnBadRequest400WhenEmailBlank() throws Exception {
        String email = " ";

        mockMvc.perform(get("/api/auth/resend/email-confirmation/{email}", email))
                .andExpect(status().isBadRequest());
    }

    @Test
    void confirmEmail_shouldReturnOk200() throws Exception {
        String token = "validToken";

        mockMvc.perform(get("/api/auth/email-confirm/{token}", token))
                .andExpect(status().isOk());
    }

    @Test
    void confirmEmail_shouldReturnBadRequest400WhenTokenBlank() throws Exception {
        String token = " ";

        mockMvc.perform(get("/api/auth/email-confirm/{token}", token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resetPasswordEmail_shouldReturnOk200() throws Exception {
        String email = "someUser@gmail.com";

        mockMvc.perform(get("/api/auth/send/reset-password-email/{email}", email))
                .andExpect(status().isOk());
    }

    @Test
    void resetPasswordEmail_shouldReturnBadRequestWhenEmailBlank() throws Exception {
        String email = " ";

        mockMvc.perform(get("/api/auth/send/reset-password-email/{email}", email))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_shouldReturnCreated201() throws Exception {
        String token = "accessToken";
        String password = "newPassword";
        ChangePasswordRequestDto passwordRequest = new ChangePasswordRequestDto(token, password);

        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void changePassword_shouldReturnBadRequestWhenPasswordBlank() throws Exception {
        String token = "accessToken";
        String password = "";
        ChangePasswordRequestDto passwordRequest = new ChangePasswordRequestDto(token, password);

        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_shouldReturnBadRequestWhenTokenAndPasswordBlank() throws Exception {
        String token = "";
        String password = "";
        ChangePasswordRequestDto passwordRequest = new ChangePasswordRequestDto(token, password);

        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCurrentUser_shouldReturnCurrentUserProfile_whenUserIsAuthenticated() throws Exception {
        when(userService.getUserProfile()).thenReturn(userDto);

        mockMvc.perform(get("/api/user/current-user")
                .with(user("email").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userDto.email()));
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() throws Exception {
        UUID uuid2 = UUID.randomUUID();
        UserDto userDto2 = new UserDto(uuid2, Role.USER, "email2", "password2");

        List<UserDto> expectedResponse = List.of(userDto, userDto2);

        when(userService.getAllUsers()).thenReturn(expectedResponse);

        MvcResult response = mockMvc.perform(get("/api/user/all")
                        .with(user("email").roles("ADMIN")))
                .andExpect(status().isOk())
                .andReturn();

        List<UserDto> actualResponse = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void ping_shouldReturnResponsePong() throws Exception {
        String expectedResponse = "pong";

        when(userService.ping()).thenReturn(expectedResponse);

        mockMvc.perform(get("/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }
}
