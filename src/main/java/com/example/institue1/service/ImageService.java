package com.example.institue1.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.baseUrl:http://localhost:8080/uploads}")
    private String baseUrl;

    // Tailles miniatures
    private static final int THUMBNAIL_WIDTH = 300;
    private static final int THUMBNAIL_HEIGHT = 200;

    /**
     * Sauvegarde une image avec génération de miniature
     */
    public Map<String, Object> saveImage(MultipartFile file) throws IOException {
        // Valider le fichier
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        // Vérifier le type MIME
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Le fichier n'est pas une image");
        }

        // Créer dossier organisé par date
        String year = String.valueOf(LocalDateTime.now().getYear());
        String month = String.format("%02d", LocalDateTime.now().getMonthValue());
        String datePath = year + "/" + month;

        Path uploadPath = Paths.get(uploadDir, datePath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Générer nom de fichier unique
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(filename);
        Path thumbnailPath = uploadPath.resolve("thumb_" + filename);

        // Lire l'image pour dimensions et redimensionnement
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Enregistrer fichier original
        Files.copy(file.getInputStream(), filePath);

        // Créer miniature
        BufferedImage thumbnail = resizeImage(originalImage, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
        String formatName = extension.replace(".", "").toLowerCase();
        ImageIO.write(thumbnail, formatName, thumbnailPath.toFile());

        // Construire URLs
        String fileUrl = baseUrl + "/" + datePath + "/" + filename;
        String thumbnailUrl = baseUrl + "/" + datePath + "/thumb_" + filename;

        // Renvoyer infos
        Map<String, Object> result = new HashMap<>();
        result.put("filename", filename);
        result.put("url", fileUrl);
        result.put("thumbnailUrl", thumbnailUrl);
        result.put("width", originalWidth);
        result.put("height", originalHeight);
        result.put("size", file.getSize());

        return result;
    }

    /**
     * Supprime une image et sa miniature
     */
    public boolean deleteImage(String filename) {
        try {
            // Recherche dans tous les dossiers par date
            File dir = new File(uploadDir);
            File[] years = dir.listFiles(File::isDirectory);
            if (years == null) return false;

            for (File year : years) {
                File[] months = year.listFiles(File::isDirectory);
                if (months == null) continue;

                for (File month : months) {
                    File file = new File(month, filename);
                    File thumbnail = new File(month, "thumb_" + filename);

                    if (file.exists()) {
                        boolean deleted = file.delete();
                        if (thumbnail.exists()) {
                            thumbnail.delete();
                        }
                        return deleted;
                    }
                }
            }

            return false;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de l'image {}: {}", filename, e.getMessage());
            return false;
        }
    }

    /**
     * Redimensionne une image tout en préservant le ratio
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        // Calculer dimensions proportionnelles
        double aspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();
        int width = targetWidth;
        int height = (int) (targetWidth / aspectRatio);

        // Ajuster si hauteur calculée dépasse la cible
        if (height > targetHeight) {
            height = targetHeight;
            width = (int) (targetHeight * aspectRatio);
        }

        // Créer nouvelle image avec transparence préservée
        BufferedImage resizedImage = new BufferedImage(width, height,
                originalImage.getTransparency() == Transparency.OPAQUE ?
                        BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);

        // Dessiner avec qualité haute
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }
}