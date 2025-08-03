package com.example.institue1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/")
public class HomeController1 {
    @GetMapping("/all")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Institut de Beauté - API OK !");
        response.put("status", "running");
        response.put("timestamp", LocalDateTime.now());
        response.put("server", "Docker Xubuntu via Git");
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("database", "MySQL 5.7 Connected");
        status.put("timestamp", LocalDateTime.now().toString());
        return status;
    }

    @GetMapping("test")
    public String test() {
        return "API Institut - Test OK depuis Docker + Git !";
    }
}
