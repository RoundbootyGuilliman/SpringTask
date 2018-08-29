package com.epam.springtask.exception;

public class ProductNotFoundException extends RuntimeException {
	
	public ProductNotFoundException(Long bookmarkId) {
		super("PRODUCT #" + bookmarkId + " NOT FOUND");
	}
}
