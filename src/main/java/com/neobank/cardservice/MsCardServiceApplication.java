package com.neobank.cardservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsCardServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsCardServiceApplication.class, args);
    }

}
