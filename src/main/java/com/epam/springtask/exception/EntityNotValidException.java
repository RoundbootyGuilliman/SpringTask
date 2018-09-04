package com.epam.springtask.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityNotValidException extends RuntimeException {
	
	public EntityNotValidException(String message) {
		super("ENTITY IS NOT VALID. ERRORS:" + message);
	}
}
