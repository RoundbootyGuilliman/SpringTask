package com.epam.springtask.util;

import com.epam.springtask.dao.UserRepository;
import com.epam.springtask.exception.EntityNotValidException;
import com.epam.springtask.exception.UserExistsException;
import com.epam.springtask.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@Service
public class Validator {
	
	private static final Logger logger = LoggerFactory.getLogger(Validator.class);
	
	private static UserRepository userRepository;
	
	public Validator(UserRepository userRepository) {
		Validator.userRepository = userRepository;
	}
	
	public static void checkForErrors(BindingResult result) {
		logger.debug("Checking BindingResult for entity validation errors");
		logger.debug("BindingResult has " + result.getErrorCount() + " errors");
		if (result.hasErrors()) {
			String message = "";
			String err = "";
			for (ObjectError error : result.getAllErrors()) {
				err = error.getObjectName() + "." + ((FieldError) error).getField() + ": " + error.getDefaultMessage() + ";";
				message = message + " " + err;
				logger.warn(err);
			}
			logger.error("EntityNotValidException has been thrown");
			throw new EntityNotValidException(message);
		}
	}
	
	public static void validateUser(String username) {
		logger.debug("Checking if \"" + username + "\" user exists");
		userRepository
				.findByName(username)
				.orElseThrow(() -> {
					logger.error("UserNotFoundException has been thrown: user \"" + username + "\" not found");
					return new UserNotFoundException(username);
				});
	}
	
	public static void validateUniqueness(String username) {
		logger.debug("Checking if \"" + username + "\" username is not yet taken");
		userRepository
				.findByName(username)
				.ifPresent((user -> {
					logger.error("UserExistsException has been thrown: user \"" + username + "\" already exists");
					throw new UserExistsException(username);
				}));
	}
}
