package com.epam.springtask.security;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;

public class CustomPermissionEvaluator implements PermissionEvaluator {
	
	@Override
	public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
		
		if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)) {
			return false;
		}
		
		return hasPrivilege(auth, targetDomainObject.toString());
	}
	
	@Override
	public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
		
		if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
			return false;
		}
		
		return hasPrivilege(auth, targetType);
	}
	
	private boolean hasPrivilege(Authentication auth, String name) {
		
		return auth.getName().contains(name) || auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
		
	}
}