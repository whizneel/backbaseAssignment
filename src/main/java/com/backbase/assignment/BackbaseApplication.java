package com.backbase.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.backbase.assignment.Repository")
public class BackbaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackbaseApplication.class, args);
    }
}
