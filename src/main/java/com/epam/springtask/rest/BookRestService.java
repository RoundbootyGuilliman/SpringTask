package com.epam.springtask.rest;

import com.epam.springtask.app.Book;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class BookRestService {
	
	@GetMapping("/book")
	public Book book(@RequestParam(value="name", defaultValue="Dune") String name) {
		return new Book(1, name);
	}
}
