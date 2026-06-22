package cm.beautysempire.institut.domain.messages;




import java.util.List;
import java.util.Optional;

public interface MessageRepositoryPort {

    Message save(Message message);

    List<Message> findAll();

    Optional <Message> findById(Long id);

    void deleteById(Long id);

    long countByStatut(StatutMessage statut);

}
