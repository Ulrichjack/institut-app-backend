package cm.beautysempire.institut.application.service;

import cm.beautysempire.institut.application.port.StoragePort;
import cm.beautysempire.institut.domain.gallery.GalleryImage;
import cm.beautysempire.institut.domain.gallery.GalleryImageRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
public class GalleryUseCase {

    private final GalleryImageRepositoryPort galleryRepositoryPort;
    private final StoragePort storagePort;

    // 1. Ajouter une image (Upload + Sauvegarde DB)
    public GalleryImage ajouterImage(MultipartFile file, GalleryImage imageDetails) {
        // 1 & 2: On upload et on récupère le tableau de résultats
        String[] uploadResult = storagePort.uploadImage(file, "ibe/galerie");

        String urlPublique = uploadResult[0]; // L'URL pour afficher l'image
        String publicId = uploadResult[1];    // L'ID secret pour pouvoir la supprimer plus tard

        // 3: On met à jour l'objet du domaine
        imageDetails.setUrl(urlPublique);
        imageDetails.setCloudinaryPublicId(publicId);

        // 4: On initialise les dates et valeurs par défaut
        imageDetails.initialiserCreation();

        // 5: On sauvegarde en base de données
        return galleryRepositoryPort.save(imageDetails);
    }

    // 2. Supprimer une image (Cloudinary + DB)
    public void supprimerImage(Long id) {
        GalleryImage image = galleryRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Image introuvable"));
        storagePort.deleteImage(image.getCloudinaryPublicId());
        galleryRepositoryPort.deleteById(id);
    }

    // 3. Lister les images publiques (Pour le site web)
    public Page<GalleryImage> listerImagesPubliques(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreation"));
        return galleryRepositoryPort.findAllPublic(pageRequest);
    }
}