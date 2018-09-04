package com.epam.springtask.util;

import com.epam.springtask.exception.EntityNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class EntityValidator {
	
	public static void checkForErrors(BindingResult result) {
		if (result.hasErrors()) {
			String message = "";
			for (ObjectError error : result.getAllErrors()) {
				message = message + " " + error.getObjectName() + "." +
						((FieldError) error).getField() + ": " + error.getDefaultMessage() + ";";
			}
			throw new EntityNotValidException(message);
		}
	}
}
