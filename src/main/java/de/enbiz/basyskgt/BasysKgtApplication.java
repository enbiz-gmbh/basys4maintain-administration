package de.enbiz.basyskgt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BasysKgtApplication implements CommandLineRunner {

	private final Logger log = LoggerFactory.getLogger(BasysKgtApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BasysKgtApplication.class, args);
	}

	@Override
	public void run(String... args) {
		// TODO
	}
}
