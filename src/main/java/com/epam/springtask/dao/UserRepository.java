package com.epam.springtask.dao;

import com.epam.springtask.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	Optional<User> findByName(String name);
}