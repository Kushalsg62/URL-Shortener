package com.kushalsg.urlshortener;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class SpringBootUrlShortenerApplicationTests {

	@Test
	void contextLoads() {
	}

}
