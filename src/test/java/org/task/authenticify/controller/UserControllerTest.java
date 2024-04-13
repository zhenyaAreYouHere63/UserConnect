package org.task.authenticify.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.task.authenticify.entity.token.TokenPair;
import org.task.authenticify.dto.user.UserDto;
import org.task.authenticify.entity.user.Role;
import org.task.authenticify.request.ChangePasswordRequest;
import org.task.authenticify.service.JwtService;
import org.task.authenticify.service.UserService;
import java.util.List;
import java.util.UUID;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
        userDto = new UserDto(uuid, Role.USER, "email", "password");

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
        ChangePasswordRequest passwordRequest = new ChangePasswordRequest(token, password);

        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void changePassword_shouldReturnBadRequestWhenPasswordBlank() throws Exception {
        String token = "accessToken";
        String password = "";
        ChangePasswordRequest passwordRequest = new ChangePasswordRequest(token, password);

        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_shouldReturnBadRequestWhenTokenAndPasswordBlank() throws Exception {
        String token = "";
        String password = "";
        ChangePasswordRequest passwordRequest = new ChangePasswordRequest(token, password);

        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCurrentUser_shouldReturnCurrentUserProfile_whenUserIsAuthenticated() throws Exception {
        UUID uuid = UUID.randomUUID();
        UserDto expectedUser = new UserDto(uuid, Role.USER, "email", "password");

        when(userService.getUserProfile()).thenReturn(expectedUser);

        mockMvc.perform(get("/api/user/current-user")
                .with(user("email").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(expectedUser.email()));
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() throws Exception {
        UUID uuid = UUID.randomUUID();
        UserDto userDto = new UserDto(uuid, Role.USER, "email", "password");

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
