package at.cosylab.cloud.acam.commons.repositories.device_type;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DeviceTypeRepository extends MongoRepository<DeviceType, String> {

    DeviceType findDeviceTypeById(String id);
    DeviceType findDeviceTypeByTypeName(String typeName);

    List<DeviceType> findDeviceTypeByServiceProvider(String serviceProvider);

}
