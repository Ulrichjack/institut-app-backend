package cm.beautysempire.institut.domain.formation;

import java.util.List;
import java.util.Optional;

public interface FormationRepositoryPort {

    Formation save(Formation formation);

    Optional<Formation> findById(Long id);

    Optional<Formation> findBySlug(String slug);

    List<Formation> findAllActive();

    List<Formation> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);

    long count();
}