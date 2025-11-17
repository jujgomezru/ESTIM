package com.estim.javaapi.controllers;

import com.estim.javaapi.application.auth.*;
import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.user.Email;
import com.estim.javaapi.domain.user.UserProfile;
import com.estim.javaapi.infrastructure.security.JwtAuthenticationProvider;
import com.estim.javaapi.infrastructure.security.SecurityContext;
import com.estim.javaapi.presentation.auth.LoginRequest;
import com.estim.javaapi.presentation.auth.RegisterUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Mocked collaborators (using new @MockitoBean) ---

    @MockitoBean
    private RegisterUserService registerUserService;

    @MockitoBean
    private AuthenticateUserService authenticateUserService;

    @MockitoBean
    private LogoutUserService logoutUserService;

    @MockitoBean
    private GetCurrentUserService getCurrentUserService;

    @MockitoBean
    private JwtAuthenticationProvider authenticationProvider;

    // ---------- Helper fixtures ----------

    /**
     * For controller tests we don't care about the internal structure of User,
     * only that the controller passes it to UserDtoMapper. So a simple mock is fine.
     */
    private User buildTestUser() {
        User user = mock(User.class);
        UserId userId = mock(UserId.class);
        Email email = mock(Email.class);
        UserProfile profile = mock(UserProfile.class);

        // id
        when(user.id()).thenReturn(userId);
        when(userId.toString()).thenReturn("user-123");

        // email
        when(user.email()).thenReturn(email);
        when(email.value()).thenReturn("john@example.com");

        // profile
        when(user.profile()).thenReturn(profile);
        when(profile.displayName()).thenReturn("John Doe");
        when(profile.avatarUrl()).thenReturn("https://example.com/avatar.png");

        // flags
        when(user.emailVerified()).thenReturn(true);

        return user;
    }

    @Nested
    @DisplayName("POST /auth/register")
    class RegisterTests {

        @Test
        @DisplayName("should create user, call RegisterUserService with correct command and return 201 + RegisterUserResponse")
        void registerSuccess() throws Exception {
            // Given
            String email = "john@example.com";
            String password = "StrongPass123!";
            String displayName = "John Doe";

            RegisterUserRequest request = new RegisterUserRequest(
                email,
                password,
                displayName
            );

            User user = buildTestUser();
            when(registerUserService.register(any(RegisterUserCommand.class)))
                .thenReturn(user);

            // When / Then
            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print()) // <--- add here
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.displayName").value(displayName))
                .andExpect(jsonPath("$.emailVerified").value(true));


            // Verify service interaction & captured command
            ArgumentCaptor<RegisterUserCommand> captor =
                ArgumentCaptor.forClass(RegisterUserCommand.class);

            verify(registerUserService).register(captor.capture());
            RegisterUserCommand cmd = captor.getValue();
            assertThat(cmd.email()).isEqualTo(email);
            assertThat(cmd.password()).isEqualTo(password);
            assertThat(cmd.displayName()).isEqualTo(displayName);
        }

        @Test
        @DisplayName("should return 400 with VALIDATION_ERROR when service throws IllegalArgumentException")
        void registerValidationError() throws Exception {
            // Given
            RegisterUserRequest request = new RegisterUserRequest(
                "invalid-email",
                "123",
                "Bad User"
            );

            when(registerUserService.register(any(RegisterUserCommand.class)))
                .thenThrow(new IllegalArgumentException("Invalid email"));

            // When / Then
            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print()) // <--- add here
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Invalid email"))
                .andExpect(jsonPath("$.details").doesNotExist());

            verify(registerUserService).register(any(RegisterUserCommand.class));
        }
    }

    // ======================================================
    //                  /auth/login
    // ======================================================

    @Nested
    @DisplayName("POST /auth/login")
    class LoginTests {

        @Test
        @DisplayName("should authenticate user, call AuthenticateUserService with correct command and return 200 + LoginResponse")
        void loginSuccess() throws Exception {
            // Given
            String email = "john@example.com";
            String password = "StrongPass123!";

            LoginRequest request = new LoginRequest(email, password);

            User user = buildTestUser();
            AuthenticationResult authResult = mock(AuthenticationResult.class);
            when(authResult.user()).thenReturn(user);
            when(authResult.accessToken()).thenReturn("access-token-123");
            when(authResult.refreshToken()).thenReturn("refresh-token-456");

            when(authenticateUserService.authenticate(any(AuthenticateUserCommand.class)))
                .thenReturn(authResult);

            // When / Then
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("access-token-123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-456"))
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.userId").value("user-123"))
                .andExpect(jsonPath("$.user.email").value("john@example.com"))
                .andExpect(jsonPath("$.user.displayName").value("John Doe"))
                .andExpect(jsonPath("$.user.emailVerified").value(true));


            // Verify service interaction & captured command
            ArgumentCaptor<AuthenticateUserCommand> captor =
                ArgumentCaptor.forClass(AuthenticateUserCommand.class);

            verify(authenticateUserService).authenticate(captor.capture());
            AuthenticateUserCommand cmd = captor.getValue();
            assertThat(cmd.email()).isEqualTo(email);
            assertThat(cmd.password()).isEqualTo(password);
        }

        @Test
        @DisplayName("should return 401 AUTH_FAILED when authentication fails with IllegalArgumentException")
        void loginFailureIllegalArgument() throws Exception {
            // Given
            LoginRequest request = new LoginRequest(
                "john@example.com",
                "wrong-password"
            );

            when(authenticateUserService.authenticate(any(AuthenticateUserCommand.class)))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

            // When / Then
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("AUTH_FAILED"))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));


            verify(authenticateUserService).authenticate(any(AuthenticateUserCommand.class));
        }

        @Test
        @DisplayName("should return 401 AUTH_FAILED when authentication fails with IllegalStateException")
        void loginFailureIllegalState() throws Exception {
            // Given
            LoginRequest request = new LoginRequest(
                "john@example.com",
                "wrong-password"
            );

            when(authenticateUserService.authenticate(any(AuthenticateUserCommand.class)))
                .thenThrow(new IllegalStateException("User disabled"));

            // When / Then
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("AUTH_FAILED"))
                .andExpect(jsonPath("$.message").value("User disabled"));

            verify(authenticateUserService).authenticate(any(AuthenticateUserCommand.class));
        }
    }

    // ======================================================
    //                  /auth/logout
    // ======================================================

    @Nested
    @DisplayName("POST /auth/logout")
    class LogoutTests {

        @Test
        @DisplayName("should extract Bearer token, pass it to LogoutUserService and return 204")
        void logoutWithToken() throws Exception {
            // Given
            String token = "jwt-token-123";

            // When / Then
            mockMvc.perform(post("/auth/logout")
                    .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isNoContent());


            // Verify that logout service receives the correct token
            verify(logoutUserService).logout(argThat(cmd ->
                cmd != null && token.equals(cmd.accessToken())
            ));
        }

        @Test
        @DisplayName("should accept missing Authorization header, send null token and return 204")
        void logoutWithoutHeader() throws Exception {
            // When / Then
            mockMvc.perform(post("/auth/logout"))
                .andDo(print())
                .andExpect(status().isNoContent());


            verify(logoutUserService).logout(argThat(cmd ->
                cmd != null && cmd.accessToken() == null
            ));
        }

        @Test
        @DisplayName("should treat non-Bearer Authorization header as null token and return 204")
        void logoutWithNonBearerHeader() throws Exception {
            // When / Then
            mockMvc.perform(post("/auth/logout")
                    .header("Authorization", "Basic abc123"))
                .andDo(print())
                .andExpect(status().isNoContent());


            verify(logoutUserService).logout(argThat(cmd ->
                cmd != null && cmd.accessToken() == null
            ));
        }

        @Test
        @DisplayName("should treat blank header as null token and return 204")
        void logoutWithBlankHeader() throws Exception {
            // When / Then
            mockMvc.perform(post("/auth/logout")
                    .header("Authorization", ""))
                .andDo(print())
                .andExpect(status().isNoContent());


            verify(logoutUserService).logout(argThat(cmd ->
                cmd != null && cmd.accessToken() == null
            ));
        }
    }

    // ======================================================
    //                  /auth/me
    // ======================================================

    @Nested
    @DisplayName("GET /auth/me")
    class MeTests {

        @Test
        @DisplayName("should authenticate from Authorization header, read current user from SecurityContext and return 200 + CurrentUserResponse")
        void meSuccess() throws Exception {
            String token = "valid-token";
            String header = "Bearer " + token;

            // authenticationProvider should succeed (no exception)
            doNothing().when(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);

            UserId userId = mock(UserId.class);
            User user = buildTestUser();
            when(getCurrentUserService.getCurrentUser(userId)).thenReturn(user);

            try (MockedStatic<SecurityContext> securityMock = mockStatic(SecurityContext.class)) {
                securityMock.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.of(userId));

                // When / Then
                mockMvc.perform(get("/auth/me")
                        .header("Authorization", header))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.userId").value("user-123"))
                    .andExpect(jsonPath("$.email").value("john@example.com"))
                    .andExpect(jsonPath("$.displayName").value("John Doe"))
                    .andExpect(jsonPath("$.avatarUrl").value("https://example.com/avatar.png"))
                    .andExpect(jsonPath("$.emailVerified").value(true));


                verify(authenticationProvider)
                    .authenticateFromAuthorizationHeader(header);
                verify(getCurrentUserService).getCurrentUser(userId);
            }

            // ClearAuthentication must be called in finally block
            verify(authenticationProvider).clearAuthentication();
        }

        @Test
        @DisplayName("should return 401 AUTH_FAILED when authenticationProvider throws IllegalArgumentException")
        void meAuthenticationFailsIllegalArgument() throws Exception {
            String header = "Bearer invalid";

            doThrow(new IllegalArgumentException("Invalid token"))
                .when(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);

            try (MockedStatic<SecurityContext> mocked = mockStatic(SecurityContext.class)) {
                mocked.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.empty());

                mockMvc.perform(get("/auth/me")
                        .header("Authorization", header))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("AUTH_FAILED"))
                    .andExpect(jsonPath("$.message").value("Invalid token"));

            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);
            verify(authenticationProvider).clearAuthentication();
            verifyNoInteractions(getCurrentUserService);
        }

        @Test
        @DisplayName("should return 401 AUTH_FAILED when SecurityContext has no current user")
        void meNoCurrentUserInContext() throws Exception {
            String header = "Bearer token-without-user";

            // authenticateFromAuthorizationHeader succeeds
            doNothing().when(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);

            try (MockedStatic<SecurityContext> mocked = mockStatic(SecurityContext.class)) {
                mocked.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.empty());

                mockMvc.perform(get("/auth/me")
                        .header("Authorization", header))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("AUTH_FAILED"))
                    .andExpect(jsonPath("$.message").value("No authenticated user"));

            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(header);
            verify(authenticationProvider).clearAuthentication();
            verifyNoInteractions(getCurrentUserService);
        }

        @Test
        @DisplayName("should return 401 AUTH_FAILED when Authorization header is missing")
        void meMissingAuthorizationHeader() throws Exception {
            // Here authenticateFromAuthorizationHeader receives null
            doThrow(new IllegalArgumentException("Missing Authorization header"))
                .when(authenticationProvider)
                .authenticateFromAuthorizationHeader(null);

            try (MockedStatic<SecurityContext> mocked = mockStatic(SecurityContext.class)) {
                mocked.when(SecurityContext::getCurrentUserId)
                    .thenReturn(Optional.empty());

                mockMvc.perform(get("/auth/me"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value("AUTH_FAILED"))
                    .andExpect(jsonPath("$.message").value("Missing Authorization header"));

            }

            verify(authenticationProvider)
                .authenticateFromAuthorizationHeader(null);
            verify(authenticationProvider).clearAuthentication();
            verifyNoInteractions(getCurrentUserService);
        }
    }
}
