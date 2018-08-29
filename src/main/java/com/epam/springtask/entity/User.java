package com.epam.springtask.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
	
	@Id @GeneratedValue
	private int id;
	private String name;
	@JsonIgnore
	private String password;
	@Enumerated(EnumType.STRING)
	private Role role;
	@OneToMany(mappedBy = "owner")
	@JsonIgnoreProperties("owner")
	private List<Product> products;
	
	public User() {
	}
	
	public User(String name, String password, Role role) {
		this.name = name;
		this.password = password;
		this.role = role;
		products = new ArrayList<>();
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
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Role getRole() {
		return role;
	}
	
	public void setRole(Role role) {
		this.role = role;
	}
	
	public List<Product> getProducts() {
		return products;
	}
	
	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
