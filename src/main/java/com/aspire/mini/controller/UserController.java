package com.aspire.mini.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aspire.mini.dto.request.UserRequestDTO;
import com.aspire.mini.dto.response.LoanResponseDTO;
import com.aspire.mini.dto.response.UserResponseDTO;
import com.aspire.mini.enums.UserRole;
import com.aspire.mini.service.UserService;
import com.aspire.mini.utility.AppUtils;
import com.aspire.mini.validator.UserValidator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

@Controller
@RequestMapping(path = "/api/v1/users")
public class UserController {

	private final UserService userService;

	private final AppUtils appUtils;

	public UserController(AppUtils appUtils, UserService userService) {
		this.userService = userService;
		this.appUtils = appUtils;
	}

	/**
	 * Create a new user with the provided user details. This endpoint is accessible
	 * only to users with administrative privileges.
	 *
	 * @param user  The UserRequestDTO containing user creation details.
	 * @param token The JWT authorization token of an administrator.
	 * @return A ResponseEntity containing the created UserResponseDTO if
	 *         successful, or a relevant error response.
	 */
	@PostMapping(path = "/createuser")
	public ResponseEntity<?> createUser(@RequestBody UserRequestDTO user,
			@RequestHeader(name = "Authorization") String token) {
		try {
			UserValidator.validateCreateUser(user);
			Jws<Claims> claims = this.appUtils.validateJWTAndReturnClaims(token);
			if (claims == null
					|| !StringUtils.pathEquals(String.valueOf(claims.getBody().get("role")), UserRole.ADMIN.toString()))
				return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
			if (hasEmptyValues(user.getEmail(), user.getPassword()) || user.getIsAdmin() == null)
				return new ResponseEntity<String>(AppUtils.MISSING_PARAMETERS, HttpStatus.UNPROCESSABLE_ENTITY);
			UserResponseDTO createdUser = this.userService.createUser(user.getEmail(), user.getPassword(),
					user.getIsAdmin());
			if (createdUser == null)
				return new ResponseEntity<String>(AppUtils.USER_ALREADY_EXISTS, HttpStatus.CONFLICT);
			return new ResponseEntity<UserResponseDTO>(createdUser, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}

	}

	/**
	 * Authenticate a user with the provided email and password.
	 *
	 * @param user The UserRequestDTO containing user authentication details.
	 * @return A ResponseEntity containing the authenticated UserResponseDTO if
	 *         successful, or a relevant error response.
	 */
	@PostMapping(path = "/authenticate")
	public ResponseEntity<?> authenticate(@RequestBody UserRequestDTO user) {
		try {
			if (hasEmptyValues(user.getEmail(), user.getPassword()))
				return new ResponseEntity<String>(AppUtils.MISSING_PARAMETERS, HttpStatus.UNPROCESSABLE_ENTITY);
			return ResponseEntity.ok(this.userService.authenticateUser(user.getEmail(), user.getPassword()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}

	}

	private boolean hasEmptyValues(String email, String password) {
		return !StringUtils.hasText(email) || !StringUtils.hasText(password);
	}

}
