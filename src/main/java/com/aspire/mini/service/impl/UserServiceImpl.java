package com.aspire.mini.service.impl;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.aspire.mini.dto.response.UserResponseDTO;
import com.aspire.mini.enums.UserRole;
import com.aspire.mini.model.User;
import com.aspire.mini.service.UserService;
import com.aspire.mini.utility.AppUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	private final Duration tokenValidity;

	private final String signingKey;

	private final String tokenIssuer;

	private final List<User> users;

	private ModelMapper modelMapper;

	public UserServiceImpl(List<User> users, AppUtils appUtils, ModelMapper modelMapper) {
		this.users = users;
		this.tokenValidity = Duration.ofHours(appUtils.getTokenExpiryHours());
		this.signingKey = appUtils.getSigningKey();
		this.tokenIssuer = appUtils.getTokenIssuer();
		this.modelMapper = modelMapper;
	}

	/**
	 * Create a new user with the provided email, password, and administrative
	 * privileges.
	 *
	 * @param email    The email of the new user.
	 * @param password The password of the new user.
	 * @param isAdmin  A boolean indicating whether the user is an administrator.
	 * @return A UserResponseDTO representing the created user, or null if the user
	 *         already exists.
	 */
	@Override
	public UserResponseDTO createUser(String email, String password, Boolean isAdmin) {
		if (doesUserExist(email)) {
			log.error("User already exists");
			return null;
		}
		User user = new User(email, password, isAdmin);
		this.users.add(user);
		log.info("User created successfully");
		return userToDto(user);
	}

	/**
	 * Authenticate a user with the provided email and password and generate a JWT
	 * token.
	 *
	 * @param email    The email of the user to authenticate.
	 * @param password The password of the user to authenticate.
	 * @return A JWT token if authentication is successful, or an error message if
	 *         authentication fails.
	 */
	@Override
	public String authenticateUser(String email, String password) {
		try {
			if (!doesUserExist(email))
				return AppUtils.USER_DOES_NOT_EXIST;
			User existingUser = verifyCredentialsAndReturnUser(email, password);
			String jwt = Jwts.builder().setIssuer(tokenIssuer).setSubject(email)
					.setExpiration(new Date(System.currentTimeMillis() + tokenValidity.toMillis()))
					.claim("role", existingUser.isAdmin() ? UserRole.ADMIN : UserRole.BASIC)
					.signWith(Keys.hmacShaKeyFor(signingKey.getBytes())).compact();
			log.info("Authentication successful");
			return jwt;
		} catch (RuntimeException e) {
			return e.getMessage();
		}
	}

	/**
	 * Map a User object to a UserResponseDTO using ModelMapper.
	 *
	 * @param user The User object to be mapped.
	 * @return A UserResponseDTO representing the mapped user.
	 */
	private UserResponseDTO userToDto(User user) {
		return this.modelMapper.map(user, UserResponseDTO.class);
	}

	/**
	 * Check if a user with the provided email already exists.
	 *
	 * @param email The email to check.
	 * @return true if a user with the email exists, false otherwise.
	 */
	private boolean doesUserExist(String email) {
		return this.users.stream().filter(user -> user.getEmail().equals(email)).collect(Collectors.toList())
				.size() > 0;
	}

	/**
	 * Verify user credentials (email and password) and return the matching user.
	 *
	 * @param email    The email of the user to verify.
	 * @param password The password of the user to verify.
	 * @return The User object if credentials are valid, or throw an exception if
	 *         not.
	 * @throws RuntimeException If credentials are invalid.
	 */
	private User verifyCredentialsAndReturnUser(String email, String password) {
		List<User> usersFound = this.users.stream()
				.filter(user -> user.getEmail().equals(email) && user.getPassword().equals(password))
				.collect(Collectors.toList());
		if (usersFound.size() != 1)
			throw new RuntimeException(AppUtils.INVALID_CREDENTIALS);
		return usersFound.get(0);
	}

}
