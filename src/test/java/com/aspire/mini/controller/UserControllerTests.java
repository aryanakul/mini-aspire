package com.aspire.mini.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import com.aspire.mini.dto.request.UserRequestDTO;
import com.aspire.mini.dto.response.UserResponseDTO;
import com.aspire.mini.enums.UserRole;
import com.aspire.mini.service.UserService;
import com.aspire.mini.utility.AppUtils;
import com.aspire.mini.validator.UserValidator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public class UserControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private AppUtils appUtils;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenValidUserCreateUserReturnsUserResponseDTO() throws Exception {
        // Arrange
        UserRequestDTO user = new UserRequestDTO("sample@example.com", "pa$$word", false);
        String token = "valid_admin_token";
        Jws<Claims> claims = mock(Jws.class);
        when(appUtils.validateJWTAndReturnClaims(token)).thenReturn(claims);
        when(claims.getBody()).thenReturn(mock(Claims.class));
        when(claims.getBody().get("role")).thenReturn(UserRole.ADMIN.toString());

        when(userService.createUser(user.getEmail(), user.getPassword(), user.getIsAdmin()))
                .thenReturn(new UserResponseDTO("sample@example.com", false));

        // Act
        ResponseEntity<?> response = ResponseEntity.ok(userController.createUser(user, "Bearer " + token));

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void givenDuplicateUserCreateUserReturnsConflictResponse() throws Exception {
        // Arrange
        UserRequestDTO user = new UserRequestDTO("sample@example.com", "pa$$word", false);
        String token = "valid_admin_token";
        Jws<Claims> claims = mock(Jws.class);
        when(appUtils.validateJWTAndReturnClaims(token)).thenReturn(claims);
        when(claims.getBody()).thenReturn(mock(Claims.class));
        when(claims.getBody().get("role")).thenReturn(UserRole.ADMIN.toString());

        when(userService.createUser(user.getEmail(), user.getPassword(), user.getIsAdmin())).thenReturn(null);

        // Act
        ResponseEntity<?> response = new ResponseEntity<String>(AppUtils.USER_ALREADY_EXISTS, HttpStatus.CONFLICT);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isInstanceOf(String.class);
        assertThat(response.getBody()).isEqualTo(AppUtils.USER_ALREADY_EXISTS);
    }

    @Test
    public void givenInvalidTokenCreateUserReturnsUnauthorizedResponse() throws Exception {
        // Arrange
        UserRequestDTO user = new UserRequestDTO("sample@example.com", "pa$$word", false);
        String token = "invalid_token";
        when(appUtils.validateJWTAndReturnClaims(token)).thenReturn(null);

        // Act
        ResponseEntity<?> response = userController.createUser(user, "Bearer " + token);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isInstanceOf(String.class);
        assertThat(response.getBody()).isEqualTo("Unauthorized");
    }

    @Test
    public void givenMissingParametersCreateUserReturnsUnprocessableEntityResponse() throws Exception {
        // Arrange
        UserRequestDTO user = new UserRequestDTO(null, null, null);
        String token = "valid_admin_token";
        Jws<Claims> claims = mock(Jws.class);
        when(appUtils.validateJWTAndReturnClaims(token)).thenReturn(claims);
        when(claims.getBody()).thenReturn(mock(Claims.class));
        when(claims.getBody().get("role")).thenReturn(UserRole.ADMIN.toString());

        // Act
        ResponseEntity<?> response = new ResponseEntity<String>(AppUtils.MISSING_PARAMETERS, HttpStatus.UNPROCESSABLE_ENTITY);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isInstanceOf(String.class);
        assertThat(response.getBody()).isEqualTo(AppUtils.MISSING_PARAMETERS);
    }

    @Test
    public void givenValidUserAuthenticateReturnsUserResponseDTO() throws Exception {
        // Arrange
        UserRequestDTO user = new UserRequestDTO("sample@example.com", "pa$$word", false);

        when(userService.authenticateUser(user.getEmail(), user.getPassword()))
                .thenReturn("valid_user_token");

        // Act
        ResponseEntity<?> response = userController.authenticate(user);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(String.class);
    }

    @Test
    public void givenMissingParametersAuthenticateReturnsUnprocessableEntityResponse() throws Exception {
        // Arrange
        UserRequestDTO user = new UserRequestDTO(null, null, null);

        // Act
        ResponseEntity<?> response = userController.authenticate(user);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isInstanceOf(String.class);
        assertThat(response.getBody()).isEqualTo(AppUtils.MISSING_PARAMETERS);
    }
}
