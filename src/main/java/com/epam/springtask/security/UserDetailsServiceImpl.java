package com.epam.springtask.security;

import com.epam.springtask.dao.UserRepository;
import com.epam.springtask.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) {
		
		com.epam.springtask.entity.User user = userRepository.findByName(username).get();
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(){{
			add(new SimpleGrantedAuthority(user.getRole().toString()));
		}};
		
		return buildUserForAuthentication(user, authorities);
	}
	
	private User buildUserForAuthentication(com.epam.springtask.entity.User user, List<GrantedAuthority> authorities) {
		
		boolean isEnabled = true;
		boolean isAccountNonExpired = true;
		boolean isCredentialsNonExpired = true;
		boolean isAccountNonLocked = true;
		
		return new User(user.getName(), user.getPassword(), isEnabled, isAccountNonExpired, isCredentialsNonExpired, isAccountNonLocked, authorities);
	}
}