package com.getourhome.agentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class AgentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentServiceApplication.class, args);
    }
    @RestController
    public static class HealthCheckController {
        @GetMapping("/actuator/health")
        public String health() {
            return "OK";
        }
    }

}
