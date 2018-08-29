package com.epam.springtask.rest;

import com.epam.springtask.dao.UserRepository;
import com.epam.springtask.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserRestController {
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping
	public Iterable<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	@GetMapping("/{id}")
	public User getUserById(@PathVariable int id) {
		return userRepository.findById(id).get();
	}
	
	@PostMapping
	public void receiveUser(User user) {
		jmsTemplate.convertAndSend("mailbox", user, message -> {
			message.setJMSType("User");
			return message;
		});
	}
	
	@PutMapping
	public void updateUser(User user) {
		receiveUser(user);
	}
	
	@DeleteMapping
	public void deleteUser(int id) {
		userRepository.delete(userRepository.findById(id).get());
	}
}
