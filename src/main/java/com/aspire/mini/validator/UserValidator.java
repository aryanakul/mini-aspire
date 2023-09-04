package com.aspire.mini.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aspire.mini.dto.request.UserRequestDTO;

public class UserValidator {
    public static void validateCreateUser(UserRequestDTO user) {
        if (user.getPassword().equals("")
                || user.getPassword().equals("user")) {
            throw new IllegalArgumentException("Invalid Password");
        }
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(user.getEmail());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid email");
        }
    }
}
