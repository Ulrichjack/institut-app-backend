package cm.beautysempire.institut.infrastructure.persistence.formation;

import cm.beautysempire.institut.domain.formation.Formation;
import cm.beautysempire.institut.domain.formation.FormationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FormationRepositoryAdapter implements FormationRepositoryPort {

    private final FormationJpaRepository jpaRepository;
    private final FormationPersistenceMapper mapper;

    @Override
    public Formation save(Formation formation) {
        FormationJpaEntity entity = mapper.toEntity(formation);
        FormationJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Formation> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Formation> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(mapper::toDomain);
    }


    @Override
    public Page<Formation> findAllActivePaginated(Pageable pageable) {
        return jpaRepository.findByActiveTrue(pageable).map(mapper::toDomain);
    }

    @Override
    public boolean existsByNom(String nom) {
        return jpaRepository.existsByNomIgnoreCase(nom);
    }

    @Override
    public Page<Formation> searchActiveFormations(String motCle, Pageable pageable) {
        return jpaRepository.searchByMotCle(motCle, pageable).map(mapper::toDomain);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countByActiveTrue() {
        return jpaRepository.countByActiveTrue();
    }

    @Override
    public Page<Formation> findAllPaginated(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Formation> searchAdminFormations(String motCle, String status, Pageable pageable) {
        return jpaRepository.searchAdminFormations(motCle, status, pageable).map(mapper::toDomain);
    }

}