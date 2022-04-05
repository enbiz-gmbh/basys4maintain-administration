package de.enbiz.basyskgt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class BasysKgtApplication implements CommandLineRunner {

	private Logger log = LoggerFactory.getLogger(BasysKgtApplication.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(BasysKgtApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO
	}
}
