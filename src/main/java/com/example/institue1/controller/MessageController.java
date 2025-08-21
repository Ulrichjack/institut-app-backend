package com.example.institue1.controller;


import com.example.institue1.dto.ApiResponse;
import com.example.institue1.dto.contactInscription.*;
import com.example.institue1.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/messages")

@Slf4j
public class MessageController {

    private final MessageService messageService;

    public MessageController (MessageService messageService){
        this.messageService = messageService;
    }

    @Operation(summary = "Créer un nouveau message de contact")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Message créé avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping("/contact")
    public ResponseEntity<ApiResponse<MessageDetailDto>> creerContact(
            @Valid @RequestBody ContactCreateDto dto,
            HttpServletRequest request) {

        log.info("Nouveau contact de: {}", dto.getEmail());



        MessageDetailDto message = messageService.creerContact(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(message, "Votre message a été envoyé avec succès. Nous vous répondrons rapidement."));
    }


    @Operation(summary = "Créer une nouvelle pré-inscription")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Pré-inscription créée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping("/pre-inscription")
    public ResponseEntity<ApiResponse<MessageDetailDto>> creerPreInscription(
            @Valid @RequestBody PreInscriptionCreateDto dto,
            HttpServletRequest request) {

        log.info("Nouvelle pré-inscription pour formation ID: {}", dto.getFormationId());


        MessageDetailDto message = messageService.creerPreInscription(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(message, "Votre pré-inscription a été enregistrée. Nous vous contacterons très prochainement."));
    }


    @Operation(summary = "Lister les messages avec pagination et filtres")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des messages récupérée")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MessageListDto>>> listerMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateCreation") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            MessageFilterDto filtres) {

        log.debug("Liste messages - page: {}, size: {}, filtres: {}", page, size, filtres);

        // Validation paramètres pagination
        if (size > 100) size = 100; // Limite sécurité

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<MessageListDto> messages = messageService.listerMessages(filtres, pageable);

        return ResponseEntity.ok(ApiResponse.success(messages, "Messages récupérés avec succès"));
    }


    @Operation(summary = "Obtenir un message par son ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message trouvé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MessageDetailDto>> obtenirMessage(@PathVariable Long id) {
        log.debug("Récupération message ID: {}", id);

        MessageDetailDto message = messageService.obtenirMessage(id);

        return ResponseEntity.ok(ApiResponse.success(message, "Message récupéré avec succès"));
    }


    @Operation(summary = "Obtenir les statistiques des messages")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistiques calculées avec succès")
    })
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<MessageStatsDto>> obtenirStatistiques() {
        log.debug("Récupération statistiques messages");

        MessageStatsDto stats = messageService.obtenirStatistiques();

        return ResponseEntity.ok(ApiResponse.success(stats, "Statistiques calculées avec succès"));
    }


    @Operation(summary = "Changer le statut d'un message")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statut modifié avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message non trouvé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Statut invalide")
    })
    @PutMapping("/{id}/statut")
    public ResponseEntity<ApiResponse<MessageDetailDto>> changerStatut(
            @PathVariable Long id,
            @Valid @RequestBody ChangeStatutDto dto,
            Principal principal) {

        String admin = (principal != null) ? principal.getName() : "dev-user";
        log.info("Changement statut message {} vers {} par {}", id, dto.getNouveauStatut(), admin);

        MessageDetailDto message = messageService.changerStatut(id, dto, admin);

        return ResponseEntity.ok(ApiResponse.success(message,
                "Statut du message changé vers: " + dto.getNouveauStatut()));
    }


    @Operation(summary = "Marquer un message comme lu")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Message marqué comme lu"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Message non trouvé")
    })
    @PatchMapping("/{id}/lu")
    public ResponseEntity<ApiResponse<Void>> marquerCommeLu(
            @PathVariable Long id,
            Principal principal) {

        String admin = (principal != null) ? principal.getName() : "dev-user";
        log.debug("Marquage message {} comme lu par {}", id, admin);

        messageService.marquerCommeLu(id, admin);

        return ResponseEntity.ok(ApiResponse.success("Message marqué comme lu"));
    }



}
