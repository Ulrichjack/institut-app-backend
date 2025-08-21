package com.example.institue1.utils;

import com.example.institue1.model.Formation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class FormationUtils {

    // === GÉNÉRATION DE SLUG ===

    /**
     * Génère un slug unique à partir du nom de formation
     */
    public String generateSlug(String nom) {
        if (nom == null || nom.isEmpty()) {
            return "formation-" + System.currentTimeMillis() % 10000;
        }

        String baseSlug = nom.toLowerCase()
                .replaceAll("[àáâãäå]", "a")
                .replaceAll("[èéêë]", "e")
                .replaceAll("[ìíîï]", "i")
                .replaceAll("[òóôõö]", "o")
                .replaceAll("[ùúûü]", "u")
                .replaceAll("[ç]", "c")
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        // Ajouter timestamp pour unicité
        return baseSlug + "-" + System.currentTimeMillis() % 10000;
    }

    // === CALCULS SOCIAL PROOF ===

    /**
     * Retourne le nombre d'inscrits à afficher selon le social proof
     */
    public Integer getNombreInscritsAffichage(Formation formation) {
        return formation.getSocialProofActif() ?
                formation.getNombreInscritsAffiche() :
                formation.getNombreInscritsReel();
    }

    /**
     * Calcule le taux de remplissage basé sur les vrais inscrits
     */
    public double getTauxRemplissageReel(Formation formation) {
        if (formation.getNombrePlaces() == 0) return 0.0;
        return (double) formation.getNombreInscritsReel() / formation.getNombrePlaces() * 100;
    }

    /**
     * Calcule le taux de remplissage affiché (pour social proof)
     */
    public double getTauxRemplissageAffiche(Formation formation) {
        if (formation.getNombrePlaces() == 0) return 0.0;
        return (double) getNombreInscritsAffichage(formation) / formation.getNombrePlaces() * 100;
    }

    /**
     * Nombre de places restantes affiché
     */
    public int getPlacesRestantesAffichees(Formation formation) {
        return Math.max(0, formation.getNombrePlaces() - getNombreInscritsAffichage(formation));
    }

    /**
     * Nombre de places restantes réelles
     */
    public int getPlacesRestantesReelles(Formation formation) {
        return Math.max(0, formation.getNombrePlaces() - formation.getNombreInscritsReel());
    }

    /**
     * Vérifie si il y a des places disponibles (basé sur vrais inscrits)
     */
    public boolean hasPlacesDisponibles(Formation formation) {
        return formation.getNombreInscritsReel() < formation.getNombrePlaces();
    }

    /**
     * Vérifie si formation complète (basé sur vrais inscrits)
     */
    public boolean isFormationComplete(Formation formation) {
        return formation.getNombreInscritsReel() >= formation.getNombrePlaces();
    }

    /**
     * Message de social proof dynamique
     */
    public String getMessageSocialProof(Formation formation) {
        if (!formation.getSocialProofActif()) return null;

        int inscritsAffiches = getNombreInscritsAffichage(formation);
        int placesRestantes = getPlacesRestantesAffichees(formation);

        if (placesRestantes <= 3 && placesRestantes > 0) {
            return String.format("🔥 %d places déjà prises — plus que %d disponibles !",
                    inscritsAffiches, placesRestantes);
        } else if (placesRestantes == 0) {
            return "⚡ Formation complète — liste d'attente disponible !";
        } else if (inscritsAffiches > 10) {
            return String.format("🎯 %d personnes déjà inscrites — rejoignez-les !", inscritsAffiches);
        } else if (inscritsAffiches > 0) {
            return String.format("✨ %d places déjà réservées", inscritsAffiches);
        } else {
            return " Soyez parmi les premiers à vous inscrire !";
        }
    }

    // === CALCULS DE PRIX ===

    /**
     * Calcul du prix avec réduction
     */
    public BigDecimal getPrixAvecReduction(Formation formation) {
        if (!isPromoActive(formation) || formation.getPourcentageReduction().equals(BigDecimal.ZERO)) {
            return formation.getPrix();
        }
        BigDecimal reduction = formation.getPrix()
                .multiply(formation.getPourcentageReduction())
                .divide(BigDecimal.valueOf(100));
        return formation.getPrix().subtract(reduction);
    }

    /**
     * Vérification si promo active
     */
    public boolean isPromoActive(Formation formation) {
        if (!formation.getEnPromotion()) return false;
        LocalDateTime now = LocalDateTime.now();
        return (formation.getDateDebutPromo() == null || now.isAfter(formation.getDateDebutPromo())) &&
                (formation.getDateFinPromo() == null || now.isBefore(formation.getDateFinPromo()));
    }

    // === GESTION DES INSCRIPTIONS ===

    /**
     * Ajoute une inscription réelle
     */
    public void ajouterInscriptionReelle(Formation formation) {
        formation.setNombreInscritsReel(formation.getNombreInscritsReel() + 1);
        formation.setNombreInscriptions(formation.getNombreInscriptions() + 1);

        // Synchroniser l'affichage si social proof désactivé
        if (!formation.getSocialProofActif()) {
            formation.setNombreInscritsAffiche(formation.getNombreInscritsReel());
        }
    }

    /**
     * Augmente le compteur de vues
     */
    public void incrementerVues(Formation formation) {
        formation.setNombreVues(formation.getNombreVues() + 1);
    }

    /**
     * Augmente le compteur de demandes d'info
     */
    public void incrementerDemandesInfo(Formation formation) {
        formation.setNombreDemandesInfo(formation.getNombreDemandesInfo() + 1);
    }

    // === VALIDATIONS BUSINESS ===

    /**
     * Valide si l'inscription est possible
     */
    public boolean peutSInscrire(Formation formation) {
        return formation.getActive() &&
                hasPlacesDisponibles(formation) &&
                !isFormationComplete(formation);
    }

    /**
     * Valide la cohérence du social proof
     */
    public boolean isSocialProofCoherent(Formation formation) {
        if (!formation.getSocialProofActif()) return true;

        // Le nombre affiché ne doit pas dépasser trop les places disponibles
        return formation.getNombreInscritsAffiche() <= formation.getNombrePlaces() * 1.2; // 20% de tolérance
    }

    /**
     * Génère une description courte pour les listes
     */
    public String getDescriptionCourte(String description, int maxLength) {
        if (description == null || description.isEmpty()) return "";
        if (description.length() <= maxLength) return description;

        // Couper à la fin d'un mot
        String truncated = description.substring(0, maxLength);
        int lastSpace = truncated.lastIndexOf(' ');
        if (lastSpace > maxLength * 0.8) { // Si l'espace est pas trop loin
            truncated = truncated.substring(0, lastSpace);
        }
        return truncated + "...";
    }

    /**
     * Calcule le niveau de popularité (pour sorting)
     */
    public int getScorePopularite(Formation formation) {
        int score = 0;
        score += formation.getNombreVues() / 10; // 1 point par 10 vues
        score += formation.getNombreInscriptions() * 5; // 5 points par inscription
        score += formation.getNombreDemandesInfo() * 2; // 2 points par demande

        // Bonus si formation presque complète
        double tauxRemplissage = getTauxRemplissageReel(formation);
        if (tauxRemplissage > 80) score += 20;
        else if (tauxRemplissage > 60) score += 10;

        // Bonus si en promotion
        if (isPromoActive(formation)) score += 15;

        return score;
    }


    public static boolean peutRecevoirPreInscriptions(Formation formation) {
        return formation != null
                && formation.getActive()
                && formation.getNombrePlaces() > formation.getNombreInscritsReel();
    }


    public static int getPlacesRestantes(Formation formation) {
        if (formation == null) return 0;
        return Math.max(0, formation.getNombrePlaces() - formation.getNombreInscritsReel());
    }



}
