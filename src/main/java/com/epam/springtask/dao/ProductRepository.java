package com.epam.springtask.dao;

import com.epam.springtask.entity.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Integer> {
	
	List<Product> findByOwnerName(String ownerName);
}