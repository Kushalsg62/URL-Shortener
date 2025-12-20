package com.kushalsg.urlshortener;

import org.springframework.boot.SpringApplication;

class TestSpringBootUrlShortenerApplication {

	public static void main(String[] args) {
		SpringApplication
				.from(SpringBootUrlShortenerApplication::main)
				.with(TestcontainersConfiguration.class)
				.run(args);
	}
}
