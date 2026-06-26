package cm.beautysempire.institut.application.port;

import org.springframework.web.multipart.MultipartFile;

public interface StoragePort {
    // Retourne un tableau de String : [0] = URL publique, [1] = Public ID (pour la suppression)
    String[] uploadImage(MultipartFile file, String dossier);

    void deleteImage(String publicId);
}