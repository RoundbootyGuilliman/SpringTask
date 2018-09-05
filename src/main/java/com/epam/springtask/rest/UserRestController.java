package com.epam.springtask.rest;

import com.epam.springtask.dao.UserRepository;
import com.epam.springtask.entity.User;
import com.epam.springtask.util.Validator;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import org.slf4j.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserRestController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);
	
	private final JmsTemplate jmsTemplate;
	private final UserRepository userRepository;
	
	public UserRestController(JmsTemplate jmsTemplate, UserRepository userRepository) {
		this.jmsTemplate = jmsTemplate;
		this.userRepository = userRepository;
	}
	
	private static Resource<User> toResource(User user) {
		logger.trace("Turning \"" + user.getName() + "\" user into resource with static toResource method");
		return new Resource<>(user,
				linkTo(methodOn(ProductRestController.class).getProductsByUser(user.getName())).withRel("products"),
				linkTo(methodOn(UserRestController.class).getUserByUsername(user.getName())).withSelfRel());
	}
	
	@GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
	public Resources<Resource<User>> getAllUsers() {
		
		logger.debug("GET request to /users, returning all corresponding resources");
		return new Resources<>(StreamSupport.stream(userRepository
				.findAll().spliterator(), false)
				.map(UserRestController::toResource)
				.collect(Collectors.toList()));
	}
	
	@GetMapping(value = "/{username}", produces = MediaTypes.HAL_JSON_VALUE)
	public Resource<User> getUserByUsername(@PathVariable String username) {
		
		logger.debug("GET request to /users/" + username);
		Validator.validateUser(username);
		
		logger.debug("User is valid, returning corresponding resource with user " + username);
		return userRepository
				.findByName(username)
				.map(UserRestController::toResource).get();
	}
	
	@PostMapping
	public ResponseEntity<?> postUser(@Valid @RequestBody User input, BindingResult result) {
		
		logger.debug("POST request to /users");
		Validator.checkForErrors(result);
		Validator.validateUniqueness(input.getName());
		
		logger.debug("Username and entity are valid, persisting new user \"" + input.getName() + "\" to the database");
		return ResponseEntity
				.created(
						URI.create(toResource(userRepository.save(input))
								.getLink(Link.REL_SELF)
								.getHref()))
				.build();
	}
	
	@PutMapping(value = "/{username}")
	public ResponseEntity<?> putUser(@PathVariable String username, @Valid @RequestBody User input, BindingResult result) {
		
		logger.debug("PUT request to /users/" + username);
		Validator.checkForErrors(result);
		Validator.validateUser(username);
		
		logger.debug("Username and entity are valid, updating user \"" + username + "\" in the database");
		input.setId(userRepository.findByName(username).get().getId());
		userRepository.save(input);
		
		return ResponseEntity.ok().build();
	}
}