package com.epam.springtask.rest;

import com.epam.springtask.dao.ProductRepository;
import com.epam.springtask.dao.UserRepository;
import com.epam.springtask.entity.Product;
import com.epam.springtask.entity.User;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;
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
	
	private static Resource<Product> toResource(Product product) {
		logger.trace("Wrapping " + product.getOwner().getName() + "'s \"" + product.getName() + "\" product in Resource and adding links");
		return new Resource<>(product,
				linkTo(methodOn(UserRestController.class).getUserByUsername(product.getOwner().getName())).withRel("user"),
				linkTo(methodOn(ProductRestController.class).getProductsByUser(product.getOwner().getName())).withRel("products"),
				linkTo(methodOn(ProductRestController.class).getProductById(product.getId())).withSelfRel());
	}
	
	@GetMapping(value = "/{username}/all", produces = MediaTypes.HAL_JSON_VALUE)
	@PreAuthorize("hasPermission(#username, '')")
	public Resources<Resource<Product>> getProductsByUser(@PathVariable String username) {
		
		logger.debug("GET request to /products/" + username);
		Validator.validateUser(username);
		
		logger.debug("User is valid, returning all corresponding resources");
		return new Resources<>(productRepository
				.findByOwnerName(username).stream()
				.map(ProductRestController::toResource)
				.collect(Collectors.toList()));
	}
	
	@GetMapping(value = "/{productId}", produces = MediaTypes.HAL_JSON_VALUE)
	public Resource<Product> getProductById(@PathVariable int productId) {
		
		logger.debug("GET request to /products/" + productId);
		
		logger.debug("User is valid, returning corresponding resource with product #" + productId);
		return productRepository
				.findById(productId)
				.map(ProductRestController::toResource)
				.orElseThrow(() -> {
					logger.error("ProductNotFoundException has been thrown: product #" + productId +
							" not found");
					return new ProductNotFoundException(productId);
				});
	}
	
	@PostMapping
	public ResponseEntity<?> postProduct(@Valid @RequestBody Product input, BindingResult result) {
		
		logger.debug("POST request to /products");
		Validator.checkForErrors(result);
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		logger.debug("Entity is valid, sending a JMS message with the new product \"" + input.getName() + "\" for persisting");
		jmsTemplate.convertAndSend("productQueue",
				Product.from(userRepository.findByName(username).get(), input));
		
		return ResponseEntity.ok().build();
	}
	
	@PutMapping("/{productId}")
	@PreAuthorize("hasPermission(#productId, '', '')")
	public ResponseEntity<?> putProduct(@PathVariable int productId, @Valid @RequestBody Product input, BindingResult result) {
		
		logger.debug("PUT request to /products/" + productId);
		Validator.checkForErrors(result);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		logger.debug("Username, product ID and entity are valid, sending a JMS message with product #" + productId + " for updating");
		input.setId(productId);
		
		userRepository.findByName(auth.getName()).ifPresent(user -> jmsTemplate.convertAndSend("productQueue",
				Product.from(productId, user, input)));
		
		return ResponseEntity.ok().build();
	}
}
