package com.gymsystem.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		io.github.cdimascio.dotenv.Dotenv.configure().ignoreIfMissing().load().entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
		SpringApplication.run(ApiApplication.class, args);
	}

}
