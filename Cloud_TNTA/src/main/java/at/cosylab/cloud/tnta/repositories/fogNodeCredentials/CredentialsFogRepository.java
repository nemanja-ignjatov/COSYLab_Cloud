package at.cosylab.cloud.tnta.repositories.fogNodeCredentials;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CredentialsFogRepository extends MongoRepository<CredentialsFogEntity, String> {

    CredentialsFogEntity findByHash(String hash);

}
