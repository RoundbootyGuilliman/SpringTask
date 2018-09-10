package com.epam.springtask.security;

import com.epam.springtask.dao.ProductRepository;
import com.epam.springtask.exception.ProductNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomPermissionEvaluator.class);
	
	@Autowired
	private ProductRepository productRepository;
	
	
	@Override
	public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
		
		if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)) {
			return false;
		}
		
		return auth.getName().contains(targetDomainObject.toString())
				|| auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
	}
	
	@Override
	public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
		
		if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
			return false;
		}
		
		int id = (int)targetId;
		
		return productRepository.findById(id).map(product -> product.getOwner().getName().equals(auth.getName())
				|| auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))).orElseThrow(() -> {
			logger.error("ProductNotFoundException has been thrown: product #" + id + " not found");
			return new ProductNotFoundException(id);
		});
	}
}