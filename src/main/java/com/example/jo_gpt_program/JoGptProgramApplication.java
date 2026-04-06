package com.example.jo_gpt_program;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.entitycom.entity")
public class JoGptProgramApplication {

    public static void main(String[] args) {
        SpringApplication.run(JoGptProgramApplication.class, args);
    }

}
