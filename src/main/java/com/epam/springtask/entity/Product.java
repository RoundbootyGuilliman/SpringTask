package com.epam.springtask.entity;

import com.epam.springtask.validation.ValidProductName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "products")
public class Product {
	
	@Id @GeneratedValue
	private int id;
	@NotEmpty(message = "{valid.notEmpty}")
	@ValidProductName(message = "{valid.productName}")
	private String name;
	@ManyToOne
	@JsonIgnoreProperties("products")
	private User owner;
	
	public Product() {
	}
	
	public Product(String name, User owner) {
		this.name = name;
		this.owner = owner;
	}
	
	public static Product from(User user, Product product) {
		return new Product(product.name, user);
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
