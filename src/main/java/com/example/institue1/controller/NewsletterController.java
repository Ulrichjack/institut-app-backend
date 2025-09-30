package com.example.institue1.controller;

import com.example.institue1.dto.NewsletterSubscribeDto;
import com.example.institue1.service.NewsletterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
public class NewsletterController {

    private final NewsletterService newsletterService;

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestBody NewsletterSubscribeDto dto) {
        String result = newsletterService.subscribeAndSendCatalogue(dto.getEmail());
        return ResponseEntity.ok(result);
    }
}