package com.windfit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WindFitApplication {

    public static void main(String[] args) {
        SpringApplication.run(WindFitApplication.class, args);
        System.out.println("==================================================================");
        System.out.println("   WINDFIT PERFORMANCE JACKETS - MODULE GIẢM GIÁ READY!            ");
        System.out.println("   API Base URL: http://localhost:8080/api/v1/phieu-giam-gia       ");
        System.out.println("==================================================================");
    }
}
