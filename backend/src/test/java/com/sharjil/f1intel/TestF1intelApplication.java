package com.sharjil.f1intel;

import org.springframework.boot.SpringApplication;

public class TestF1intelApplication {

	public static void main(String[] args) {
		SpringApplication.from(F1intelApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
