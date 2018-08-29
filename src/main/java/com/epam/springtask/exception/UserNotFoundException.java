package com.epam.springtask.exception;

public class UserNotFoundException extends RuntimeException {
	
	public UserNotFoundException(String username) {
		super("USER " + username + " NOT FOUND");
	}
}
