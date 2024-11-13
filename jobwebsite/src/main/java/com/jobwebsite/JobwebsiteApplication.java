package com.jobwebsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class JobwebsiteApplication {

	public static void main(String[] args) {

		SpringApplication app= new SpringApplication(JobwebsiteApplication.class);

		ConfigurableApplicationContext context = app.run(args);
		String localhostLink = "http://localhost:8080";
		System.out.println("Application is running at : "+localhostLink);
	}

}
