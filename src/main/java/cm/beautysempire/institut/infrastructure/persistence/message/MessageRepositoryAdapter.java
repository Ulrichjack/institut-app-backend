package cm.beautysempire.institut.infrastructure.persistence.message;

import cm.beautysempire.institut.domain.messages.Message;
import cm.beautysempire.institut.domain.messages.MessageRepositoryPort;
import cm.beautysempire.institut.domain.messages.StatutMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryAdapter implements MessageRepositoryPort {

    private final MessageJpaRepository jpaRepository;
    private final MessagePersistenceMapper mapper;

    @Override
    public Message save(Message message) {
        MessageJpaEntity entity = mapper.toEntity(message);
        MessageJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Message> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Message> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public long countByStatut(StatutMessage statut) {
        return jpaRepository.countByStatut(statut);
    }
}