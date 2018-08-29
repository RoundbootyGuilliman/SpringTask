package com.epam.springtask.jms;

import com.epam.springtask.dao.ProductRepository;
import com.epam.springtask.dao.UserRepository;
import com.epam.springtask.entity.Product;
import com.epam.springtask.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@JmsListener(destination = "mailbox", containerFactory = "myFactory", selector = "JMSType = 'User'")
	public void receiveUser(User user) {
		System.out.println("Received <" + user + ">");
		if (user.getId() != 0) {
			user.setProducts(userRepository.findById(user.getId()).get().getProducts());
		}
		userRepository.save(user);
	}
	
	@JmsListener(destination = "mailbox", containerFactory = "myFactory", selector = "JMSType = 'Product'")
	public void receiveProduct(Product product) {
		System.out.println("Received <" + product + ">");
		if (product.getId() != 0) {
			product.setOwner(productRepository.findById(product.getId()).get().getOwner());
		} else {
			product.setOwner(userRepository.findById(product.getOwner().getId()).get());
		}
		productRepository.save(product);
	}
}
