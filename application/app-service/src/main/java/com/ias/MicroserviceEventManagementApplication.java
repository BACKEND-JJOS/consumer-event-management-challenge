package com.ias;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

@SpringBootApplication(scanBasePackages = "com.ias")
public class MicroserviceEventManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceEventManagementApplication.class, args);
    }

}