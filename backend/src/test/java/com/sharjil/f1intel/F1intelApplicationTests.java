package com.sharjil.f1intel;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class F1intelApplicationTests {

	@Test
	void contextLoads() {
	}

}
