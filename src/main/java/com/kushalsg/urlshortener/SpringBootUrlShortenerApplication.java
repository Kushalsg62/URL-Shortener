package com.kushalsg.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@ConfigurationPropertiesScan
public class SpringBootUrlShortenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootUrlShortenerApplication.class, args);
	}

}
