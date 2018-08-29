package com.epam.springtask.exception;

public class UserExistsException extends RuntimeException {
	
	public UserExistsException(String username) {
		super("USER " + username + " ALREADY EXISTS");
	}
}
