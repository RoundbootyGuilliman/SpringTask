package com.epam.springtask.rest;

import com.epam.springtask.dao.UserRepository;
import com.epam.springtask.entity.User;
import com.epam.springtask.exception.UserExistsException;
import com.epam.springtask.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserRestController {
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private UserRepository userRepository;
	
	private static Resource<User> toResource(User user) {
		return new Resource<>(user,
				linkTo(methodOn(ProductRestController.class).getProductsByUser(user.getName())).withRel("products"),
				linkTo(methodOn(UserRestController.class).getUserByUsername(user.getName())).withSelfRel());
	}
	
	private void validateUser(String username) {
		userRepository
				.findByName(username)
				.orElseThrow(() -> new UserNotFoundException(username));
	}
	
	private void validateUniqueness(String username) {
		userRepository
				.findByName(username)
				.ifPresent((user -> {
					throw new UserExistsException(username);
				}));
	}
	
	@GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
	public Resources<Resource<User>> getAllUsers() {
		
		return new Resources<>(StreamSupport.stream(userRepository
				.findAll().spliterator(), false)
				.map(UserRestController::toResource)
				.collect(Collectors.toList()));
	}
	
	@GetMapping(value = "/{username}", produces = MediaTypes.HAL_JSON_VALUE)
	public Resource<User> getUserByUsername(@PathVariable String username) {
		
		validateUser(username);
		
		return userRepository
				.findByName(username)
				.map(UserRestController::toResource)
				.orElseThrow(() -> new UserNotFoundException(username));
	}
	
	@PostMapping
	public ResponseEntity<?> postUser(@RequestBody User input) {
		
		validateUniqueness(input.getName());
		
		return ResponseEntity.created(
				URI.create(toResource(userRepository.save(input))
						.getLink(Link.REL_SELF)
						.getHref()))
				.build();
	}
}
