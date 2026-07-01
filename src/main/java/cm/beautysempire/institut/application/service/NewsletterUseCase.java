package cm.beautysempire.institut.application.service;

import cm.beautysempire.institut.application.port.WhatsAppNotifierPort;
import cm.beautysempire.institut.domain.newsletter.NewsletterSubscription;
import cm.beautysempire.institut.domain.newsletter.NewsletterRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class NewsletterUseCase {

    private final NewsletterRepositoryPort repositoryPort;
    private final WhatsAppNotifierPort whatsAppNotifierPort;

    public NewsletterSubscription souscrire(NewsletterSubscription subscription) {
        // On évite les doublons de numéros de téléphone
        if (repositoryPort.existsByTelephone(subscription.getTelephone())) {
            throw new IllegalArgumentException("Ce numéro de téléphone est déjà inscrit.");
        }
        subscription.initialiser();
        return repositoryPort.save(subscription);
    }

    public Page<NewsletterSubscription> listerAbonnes(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateInscription"));
        return repositoryPort.findAll(pageRequest);
    }

    public String genererLienWhatsAppCatalogue() {
        return whatsAppNotifierPort.genererLienCatalogue();
    }

    public NewsletterSubscription marquerCommeContacte(Long id) {
        NewsletterSubscription subscription = repositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Abonnement introuvable avec l'ID : " + id));
        subscription.marquerCommeContacte();
        return repositoryPort.save(subscription);
    }
}