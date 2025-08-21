package com.example.institue1.repository;

import com.example.institue1.model.WhatsAppMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WhatsAppRepository extends JpaRepository<WhatsAppMessage, Long> {
    // Trouver les messages liés à un message spécifique
    List<WhatsAppMessage> findByMessageIdOrderByDateCreationDesc(Long messageId);

    // Trouver les messages non envoyés pour réessayer
    List<WhatsAppMessage> findByEnvoyeFalseOrderByDateCreationAsc();

    // Compter les messages par destinataire sur une période (éviter spam)
    Long countByDestinaireAndDateCreationBetween(String destinataire,
                                                 LocalDateTime debut,
                                                 LocalDateTime fin);
}