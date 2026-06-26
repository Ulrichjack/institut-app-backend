package cm.beautysempire.institut.domain.messages;




import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MessageRepositoryPort {

    Message save(Message message);

    Page<Message> findAll(Pageable pageable);

    Optional <Message> findById(Long id);

    void deleteById(Long id);

    long countByStatut(StatutMessage statut);

}
