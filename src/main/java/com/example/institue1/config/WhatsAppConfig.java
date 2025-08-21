package com.example.institue1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.whatsapp")
@Data
public class WhatsAppConfig {
    private boolean enabled = false;
    private String apiUrl;
    private String apiToken;
    private String businessPhone;
    private int maxDailyMessages = 10; // Limite par destinataire
    private String defaultTemplate = "Bonjour {nom}, {message}";
}