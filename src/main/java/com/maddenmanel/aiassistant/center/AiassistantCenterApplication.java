package com.maddenmanel.aiassistant.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AiassistantCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiassistantCenterApplication.class, args);
    }

}
