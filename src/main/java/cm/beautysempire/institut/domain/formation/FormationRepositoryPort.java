package cm.beautysempire.institut.domain.formation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FormationRepositoryPort {

    Formation save(Formation formation);

    Optional<Formation> findById(Long id);

    Optional<Formation> findBySlug(String slug);

    Page<Formation> findAllActivePaginated(Pageable pageable);

    boolean existsByNom(String nom);

    Page<Formation> searchActiveFormations(String motCle, Pageable pageable);

    long count();

    long countByActiveTrue();


    Page<Formation> findAllPaginated(Pageable pageable);

    Page<Formation> searchAdminFormations(String motCle, String status, Pageable pageable);

}