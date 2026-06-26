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

        // 🔥 On utilise le mapper au lieu du Builder manuel !
        MessageResponse response = messageApiMapper.toResponse(savedMessage, whatsappLink);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Message soumis avec succès"));
    }

    @PostMapping("/pre-inscription")
    public ResponseEntity<ApiResponse<MessageResponse>> soumettrePreInscription(@Valid @RequestBody PreInscriptionRequest request) {

        Message message = messageApiMapper.toDomain(request);
        Message savedMessage = messageUseCase.soumettrePreInscription(message, request.getFormationId());
        String whatsappLink = messageUseCase.genererLienWhatsAppClient(savedMessage);

        // 🔥 On utilise le mapper ici aussi !
        MessageResponse response = messageApiMapper.toResponse(savedMessage, whatsappLink);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Pré-inscription enregistrée avec succès"));
    }


    @PatchMapping("/{id}/lu")
    public ResponseEntity<ApiResponse<Message>> marquerCommeLu(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(messageUseCase.marquerCommeLu(id, "admin"), "Message marqué comme lu"));
    }

    @GetMapping("/stats/non-lus")
    public ResponseEntity<ApiResponse<Long>> compterNonLus() {

       Long response =  messageUseCase.compterMessagesNonLus();
        return ResponseEntity.ok(ApiResponse.success(response, "Static recupere avec success"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MessageListResponse>>> listerMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Message> messagesPage = messageUseCase.listerMessagesPagines(page, size);
        Page<MessageListResponse> responsePage = messagesPage.map(messageApiMapper::toListResponse);

        return ResponseEntity.ok(ApiResponse.success(responsePage, "Liste des messages récupérée"));
    }

    @PatchMapping("/{id}/traite")
    public ResponseEntity<ApiResponse<Message>> marquerCommeTraite(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(messageUseCase.marquerCommeTraite(id, "admin"), "Message marqué comme traité"));
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<Message>> marquerCommeArchive(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(messageUseCase.marquerCommeArchive(id, "admin"), "Message archivé"));
    }


}