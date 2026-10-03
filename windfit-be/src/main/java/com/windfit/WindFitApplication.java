package com.windfit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WindFitApplication {

    public static void main(String[] args) {
        SpringApplication.run(WindFitApplication.class, args);
    }
}
