package com.windfit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WindFitApplication {

    public static void main(String[] args) {
        SpringApplication.run(WindFitApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("   WINDFIT PERFORMANCE JACKETS - BACKEND SYSTEM STARTED SUCCESSFULLY!   ");
        System.out.println("   API Base URL: http://localhost:8080/api/v1/hoa-don             ");
        System.out.println("==================================================================");
    }
}
