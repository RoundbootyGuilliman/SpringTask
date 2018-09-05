package com.epam.springtask.rest;

import com.epam.springtask.dao.ProductRepository;
import com.epam.springtask.dao.UserRepository;
import com.epam.springtask.entity.Product;
import com.epam.springtask.exception.ProductNotFoundException;
import com.epam.springtask.util.Validator;
import org.slf4j.Logger;
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
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/products")
public class ProductRestController {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductRestController.class);
	
	private final JmsTemplate jmsTemplate;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	
	public ProductRestController(JmsTemplate jmsTemplate, ProductRepository productRepository, UserRepository userRepository) {
		this.jmsTemplate = jmsTemplate;
		this.productRepository = productRepository;
		this.userRepository = userRepository;
	}
	
	private static Resource<Product> toResource(Product product, String username) {
		logger.trace("Turning " + username + "'s \"" + product.getName() + "\" product into resource with static toResource method");
		return new Resource<>(product,
				linkTo(methodOn(UserRestController.class).getUserByUsername(username)).withRel("user"),
				linkTo(methodOn(ProductRestController.class).getProductsByUser(username)).withRel("products"),
				linkTo(methodOn(ProductRestController.class).getProductByUserAndId(username, product.getId())).withSelfRel());
	}
	
	@GetMapping(value = "/{username}", produces = MediaTypes.HAL_JSON_VALUE)
	public Resources<Resource<Product>> getProductsByUser(@PathVariable String username) {
		
		logger.debug("GET request to /products/" + username);
		Validator.validateUser(username);
		
		logger.debug("User is valid, returning all corresponding resources");
		return new Resources<>(productRepository
				.findByOwnerName(username).stream()
				.map(product -> toResource(product, username))
				.collect(Collectors.toList()));
	}
	
	@GetMapping(value = "/{username}/{productId}", produces = MediaTypes.HAL_JSON_VALUE)
	public Resource<Product> getProductByUserAndId(@PathVariable String username, @PathVariable int productId) {
		
		logger.debug("GET request to /products/" + username + "/" + productId);
		Validator.validateUser(username);
		
		logger.debug("User is valid, returning corresponding resource with product #" + productId);
		return productRepository
				.findById(productId)
				.map(product -> toResource(product, username))
				.orElseThrow(() -> {
					logger.error("ProductNotFoundException has been thrown: product #" + productId + " not found");
					return new ProductNotFoundException(productId);
				});
	}
	
	@PostMapping("/{username}")
	public ResponseEntity<?> postProduct(@PathVariable String username,
										 @Valid @RequestBody Product input, BindingResult result) {
		
		logger.debug("POST request to /products/" + username);
		Validator.checkForErrors(result);
		Validator.validateUser(username);
		
		logger.debug("User and entity are valid, persisting new product \"" + input.getName() + "\" to the database");
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
	
	@PutMapping("/{username}/{productId}")
	public ResponseEntity<?> putProduct(@PathVariable String username, @PathVariable int productId,
										@Valid @RequestBody Product input, BindingResult result) {
		
		logger.debug("PUT request to /products/" + username + "/" + productId);
		Validator.checkForErrors(result);
		Validator.validateUser(username);
		
		logger.debug("User and entity are valid, updating product #" + productId + " in the database");
		input.setId(productId);
		productRepository.save(input);
		
		return ResponseEntity.ok().build();
	}
}
