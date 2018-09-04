package com.epam.springtask.entity;

import com.epam.springtask.validation.ValidPassword;
import com.epam.springtask.validation.ValidUsername;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
	
	@Id
	@GeneratedValue
	private int id;
	
	@NotEmpty(message = "{valid.notEmpty}")
	@ValidUsername(message = "{valid.username}")
	private String name;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotEmpty(message = "{valid.notEmpty}")
	@ValidPassword(message = "{valid.password}")
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
	
	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", name='" + name + '\'' +
				", password='" + password + '\'' +
				", role=" + role +
				", products=" + products +
				'}';
	}
}
