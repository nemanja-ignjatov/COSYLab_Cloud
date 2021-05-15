package at.cosylab.cloud.tnta.repositories.fogNode;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FogNodeRepository extends MongoRepository<FogNodeEntity, String> {

    FogNodeEntity findByCsrToken(String csrToken);

    List<FogNodeEntity> findAllByCertificateRevokedAtAfter(LocalDateTime lastSync);

    FogNodeEntity findByIdentity(String identity);

}
