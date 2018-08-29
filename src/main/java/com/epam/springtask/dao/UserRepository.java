package com.epam.springtask.dao;

import com.epam.springtask.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {
	
	List<User> findByName(String name);
}