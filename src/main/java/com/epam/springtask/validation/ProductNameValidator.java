package com.epam.springtask.validation;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductNameValidator implements ConstraintValidator<ValidProductName, String> {
	
	private Pattern pattern;
	private Matcher matcher;
	
	private static final String PRODUCT_NAME_PATTERN = "[a-zA-Z]{1}[\\w_ ]{2,19}";
	
	@Override
	public void initialize(ValidProductName constraintAnnotation) {
	}
	
	@Override
	public boolean isValid(String name, ConstraintValidatorContext context){
		
		if (StringUtils.isEmpty(name)) {
			return true;
		}
		return (validateProductName(name));
	}
	
	private boolean validateProductName(String name) {
		pattern = Pattern.compile(PRODUCT_NAME_PATTERN);
		matcher = pattern.matcher(name);
		return matcher.matches();
	}
}