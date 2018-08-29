package com.epam.springtask.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {
	
	public ProductNotFoundException(int bookmarkId) {
		super("PRODUCT #" + bookmarkId + " NOT FOUND");
	}
}
