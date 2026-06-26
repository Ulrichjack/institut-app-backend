package cm.beautysempire.institut.infrastructure.storage;

import cm.beautysempire.institut.application.port.StoragePort;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CloudinaryStorageAdapter implements StoragePort {

    private final Cloudinary cloudinary;

    @Override
    public String[] uploadImage(MultipartFile file, String dossier) {
        try {
            // On demande à Cloudinary de compresser l'image (quality: auto) et de choisir le meilleur format (fetch_format: auto)
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", dossier,
                    "quality", "auto:good",
                    "fetch_format", "auto"
            ));

            String url = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

            log.info("Image uploadée sur Cloudinary avec succès. Public ID: {}", publicId);
            return new String[]{url, publicId};

        } catch (IOException e) {
            log.error("Erreur lors de l'upload sur Cloudinary", e);
            throw new RuntimeException("Échec de l'upload de l'image", e);
        }
    }

    @Override
    public void deleteImage(String publicId) {
        if (publicId == null || publicId.isEmpty()) {
            return;
        }

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Image supprimée de Cloudinary. Public ID: {}", publicId);
        } catch (Exception e) {
            // On log l'erreur, mais on ne fait pas planter l'application.
            // Il vaut mieux avoir une image "orpheline" sur Cloudinary qu'une erreur 500 pour l'admin.
            log.error("Erreur lors de la suppression sur Cloudinary pour le Public ID: {}", publicId, e);
        }
    }
}