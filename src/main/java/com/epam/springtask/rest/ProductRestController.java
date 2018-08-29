package com.epam.springtask.rest;

import com.epam.springtask.dao.ProductRepository;
import com.epam.springtask.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductRestController {
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private ProductRepository productRepository;
	
	@GetMapping
	public Iterable<Product> getAllProducts() {
		return productRepository.findAll();
	}
	
	@GetMapping("/{id}")
	public Product getProductById(@PathVariable int id) {
		return productRepository.findById(id).get();
	}
	
	@PostMapping
	public void receiveProduct(Product product) {
		jmsTemplate.convertAndSend("mailbox", product, message -> {
			message.setJMSType("Product");
			return message;
		});
	}
	
	@PutMapping
	public void updateProduct(Product product) {
		receiveProduct(product);
	}
	
	@DeleteMapping
	public void deleteProduct(int id) {
		productRepository.delete(productRepository.findById(id).get());
	}
}
