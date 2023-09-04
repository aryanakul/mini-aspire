package com.aspire.mini.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {

	private final String email;
	private String password;
	private boolean isAdmin;

}
