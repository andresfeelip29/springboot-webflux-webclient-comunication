package com.co.app.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SpringbootRestWebfluxClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootRestWebfluxClientApplication.class, args);
    }

}
