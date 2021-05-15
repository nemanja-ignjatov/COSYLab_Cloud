package at.cosylab.cloud.acam.commons.repositories.device_type;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DeviceTypeVersionRepository extends MongoRepository<VersionChangeLog, String> {

    VersionChangeLog findVersionChangeLogById(String id);
    List<VersionChangeLog> findVersionChangeLogByDeviceTypeId(String typeName);
}
