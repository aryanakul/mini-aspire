package com.aspire.mini.service;

import com.aspire.mini.dto.response.UserResponseDTO;

public interface UserService {

	public UserResponseDTO createUser(String username, String password, Boolean isAdmin);

	public String authenticateUser(String username, String password);

}
