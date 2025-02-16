package com.donut.swaipe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SwaipeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwaipeApplication.class, args);
    }

}
