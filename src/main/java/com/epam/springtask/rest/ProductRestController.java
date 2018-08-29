package com.epam.springtask.rest;

import com.epam.springtask.dao.ProductRepository;
import com.epam.springtask.dao.UserRepository;
import com.epam.springtask.entity.Product;
import com.epam.springtask.exception.ProductNotFoundException;
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

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/products")
public class ProductRestController {
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private static Resource<Product> toResource(Product product, String username) {
		return new Resource<>(product,
				linkTo(methodOn(UserRestController.class).getUserByUsername(username)).withRel("user"),
				linkTo(methodOn(ProductRestController.class).getProductsByUser(username)).withRel("products"),
				linkTo(methodOn(ProductRestController.class).getProductByUserAndId(username, product.getId())).withSelfRel());
	}
	
	private void validateUser(String username) {
		userRepository
				.findByName(username)
				.orElseThrow(() -> new UserNotFoundException(username));
	}
	
	@GetMapping(value = "/{username}", produces = MediaTypes.HAL_JSON_VALUE)
	public Resources<Resource<Product>> getProductsByUser(@PathVariable String username) {
		
		validateUser(username);
		
		return new Resources<>(productRepository
				.findByOwnerName(username).stream()
				.map(product -> toResource(product, username))
				.collect(Collectors.toList()));
	}
	
	@GetMapping(value = "/{username}/{productId}", produces = MediaTypes.HAL_JSON_VALUE)
	public Resource<Product> getProductByUserAndId(@PathVariable String username, @PathVariable int productId) {
		validateUser(username);
		
		return productRepository
				.findById(productId)
				.map(product -> toResource(product, username))
				.orElseThrow(() -> new ProductNotFoundException(productId));
	}
	
	@PostMapping("/{username}")
	public ResponseEntity<?> postProduct(@PathVariable String username, @RequestBody Product input) {
		
		validateUser(username);
		
		return userRepository
				.findByName(username)
				.map(user -> ResponseEntity.created(
						URI.create(
								toResource(
										productRepository.save(Product.from(user, input)), username)
										.getLink(Link.REL_SELF)
										.getHref()))
						.build())
				.orElse(ResponseEntity.noContent().build());
	}
}
