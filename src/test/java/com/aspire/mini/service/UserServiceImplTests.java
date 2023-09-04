package com.aspire.mini.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.aspire.mini.model.User;
import com.aspire.mini.service.impl.UserServiceImpl;
import com.aspire.mini.utility.AppUtils;

public class UserServiceImplTests {

	private UserService userService;

	@BeforeEach
	public void setUp() {
		AppUtils appUtils = new AppUtils(1, "fhbfjsbfjjhewjfbefbjhwehjfrebfjfhbjherbhjerbwhjfbhjrefb",
				"http://mini-aspire.com");
		ModelMapper modelMapper = new ModelMapper();
		userService = new UserServiceImpl(new ArrayList<User>(), appUtils, modelMapper);
	}

	@Test
	public void givenEmailPasswordAndIsAdminCreateAndReturnUser() throws Exception {
		assertThat(userService.createUser("sample@example.com", "pa$$word", false)).hasFieldOrPropertyWithValue("email",
				"sample@example.com");
	}

	@Test
	public void givenDuplicateUserFailUserCreation() throws Exception {
		userService.createUser("sample@example.com", "pa$$word", false);
		assertThat(userService.createUser("sample@example.com", "wordpa$$", false)).isNull();
	}

}
