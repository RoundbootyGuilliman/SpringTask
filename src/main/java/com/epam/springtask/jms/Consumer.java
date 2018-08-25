package com.epam.springtask.jms;

import com.epam.springtask.app.Book;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
	
	@JmsListener(destination = "mailbox", containerFactory = "myFactory")
	public void receiveMessage(Book book) {
		System.out.println("Received <" + book + ">");
	}
}
