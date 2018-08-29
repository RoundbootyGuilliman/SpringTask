package com.epam.springtask.dao;

import com.epam.springtask.entity.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Integer> {
	
	List<Product> findByName(String name);
}