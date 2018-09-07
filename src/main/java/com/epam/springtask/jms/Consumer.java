package com.epam.springtask.jms;

import com.epam.springtask.dao.ProductRepository;
import com.epam.springtask.dao.UserRepository;
import com.epam.springtask.entity.Product;
import com.epam.springtask.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
	
	private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@JmsListener(destination = "mailbox", containerFactory = "myFactory", selector = "JMSType = 'User'")
	public void receiveUser(User user) {
		logger.debug("Message with \"" + user.getName() + "\" user received");
		userRepository.save(user);
	}
	
	@JmsListener(destination = "mailbox", containerFactory = "myFactory", selector = "JMSType = 'Product'")
	public void receiveProduct(Product product) {
		logger.debug("Message with \"" + product.getName() + "\" product received");
		productRepository.save(product);
	}
}
