package com.aspire.mini;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aspire.mini.model.Loan;
import com.aspire.mini.model.User;

@Configuration
public class SeedingApplicationDataConfiguration {

	@Bean
	public List<User> applicationUsers() {
		final List<User> users = new ArrayList<>();
		User superUser = new User("admin@mini-aspire.com", "admin", true);
		users.add(superUser);
		return users;
	}

	@Bean
	public List<Loan> loans() {
		final List<Loan> loans = new ArrayList<>();
		return loans;
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
