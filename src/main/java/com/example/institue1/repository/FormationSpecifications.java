package com.example.institue1.repository;

import com.example.institue1.model.Formation;
import org.springframework.data.jpa.domain.Specification;

/**
 * Version SIMPLIFIÉE - Juste pour la recherche unifiée
 */
public class FormationSpecifications {

    /**
     * Formation active uniquement
     */
    public static Specification<Formation> estActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("active"), true);
    }

    /**
     * RECHERCHE UNIFIÉE - Cherche partout avec un seul terme
     * Cherche dans : nom, description, catégorie, objectifs
     */
    public static Specification<Formation> rechercheGlobale(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String likePattern = "%" + searchTerm.toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("nom")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("categorie")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("objectifs")), likePattern)

            );
        };
    }

    /**
     * Combinaison pour recherche publique
     */
    public static Specification<Formation> recherchePublique(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return estActive(); // Toutes les formations actives
        }
        return estActive().and(rechercheGlobale(searchTerm));
    }

    // SUPPRIMER TOUT LE RESTE des specifications
}