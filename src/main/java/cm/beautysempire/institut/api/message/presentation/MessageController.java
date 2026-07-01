package cm.beautysempire.institut.api.message.presentation;

import cm.beautysempire.institut.api.shared.ApiResponse;
import cm.beautysempire.institut.application.service.MessageUseCase;
import cm.beautysempire.institut.domain.messages.Message;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageUseCase messageUseCase;
    private final MessageApiMapper messageApiMapper;

    @PostMapping("/contact")
    public ResponseEntity<ApiResponse<MessageResponse>> soumettreContact(@Valid @RequestBody ContactCreateRequest request) {
        Message message = messageApiMapper.toDomain(request);
        Message savedMessage = messageUseCase.soumettreContact(message);
        String whatsappLink = messageUseCase.genererLienWhatsAppClient(savedMessage);

        MessageResponse response = messageApiMapper.toResponse(savedMessage, whatsappLink);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Message soumis avec succès"));
    }

    @PostMapping("/pre-inscription")
    public ResponseEntity<ApiResponse<MessageResponse>> soumettrePreInscription(@Valid @RequestBody PreInscriptionRequest request) {
        Message message = messageApiMapper.toDomain(request);
        Message savedMessage = messageUseCase.soumettrePreInscription(message, request.getFormationId());
        String whatsappLink = messageUseCase.genererLienWhatsAppClient(savedMessage);

        MessageResponse response = messageApiMapper.toResponse(savedMessage, whatsappLink);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Pré-inscription enregistrée avec succès"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MessageListResponse>>> listerMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Message> messagesPage = messageUseCase.listerMessagesPagines(page, size);
        Page<MessageListResponse> responsePage = messagesPage.map(messageApiMapper::toListResponse);

        return ResponseEntity.ok(ApiResponse.success(responsePage, "Liste des messages récupérée"));
    }

    @GetMapping("/stats/non-lus")
    public ResponseEntity<ApiResponse<Long>> compterNonLus() {
        Long response = messageUseCase.compterMessagesNonLus();
        return ResponseEntity.ok(ApiResponse.success(response, "Statistiques récupérées avec succès"));
    }

    @PatchMapping("/{id}/lu")
    public ResponseEntity<ApiResponse<MessageListResponse>> marquerCommeLu(@PathVariable Long id) {
        Message message = messageUseCase.marquerCommeLu(id, "admin");
        MessageListResponse response = messageApiMapper.toListResponse(message);
        return ResponseEntity.ok(ApiResponse.success(response, "Message marqué comme lu"));
    }


    @PatchMapping("/{id}/traite")
    public ResponseEntity<ApiResponse<MessageListResponse>> marquerCommeTraite(@PathVariable Long id) {
        Message message = messageUseCase.marquerCommeTraite(id, "admin");
        MessageListResponse response = messageApiMapper.toListResponse(message);
        return ResponseEntity.ok(ApiResponse.success(response, "Message marqué comme traité"));
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<MessageListResponse>> marquerCommeArchive(@PathVariable Long id) {
        Message message = messageUseCase.marquerCommeArchive(id, "admin");
        MessageListResponse response = messageApiMapper.toListResponse(message);
        return ResponseEntity.ok(ApiResponse.success(response, "Message archivé"));
    }
}