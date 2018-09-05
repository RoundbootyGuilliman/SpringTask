package com.epam.springtask.entity;

import com.epam.springtask.app.Application;
import com.epam.springtask.validation.ValidProductName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "products")
public class Product {
	
	private static final Logger logger = LoggerFactory.getLogger(Product.class);
	
	@Id @GeneratedValue
	private int id;
	@NotEmpty(message = "{valid.notEmpty}")
	@ValidProductName(message = "{valid.productName}")
	private String name;
	@ManyToOne
	@JsonIgnoreProperties("products")
	private User owner;
	
	public Product() {
		logger.trace("Creating Product instance via default constructor");
	}
	
	public Product(String name, User owner) {
		logger.trace("Creating Product instance via constructor with " + name + " as name and " + owner.getName() + " as owner");
		this.name = name;
		this.owner = owner;
	}
	
	public static Product from(User owner, Product product) {
		logger.trace("Creating Product instance via static method with " + product.getName() + " as name and " + owner.getName() + " as owner");
		return new Product(product.name, owner);
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public User getOwner() {
		return owner;
	}
	
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	@Override
	public String toString() {
		return "Product{" +
				"id=" + id +
				", name='" + name + '\'' +
				", owner=" + owner +
				'}';
	}
}
