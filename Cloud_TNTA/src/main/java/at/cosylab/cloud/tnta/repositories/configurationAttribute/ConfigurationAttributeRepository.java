package at.cosylab.cloud.tnta.repositories.configurationAttribute;


import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigurationAttributeRepository extends MongoRepository<ConfigurationAttribute, String> {

    ConfigurationAttribute findByName(String attrName);
}
