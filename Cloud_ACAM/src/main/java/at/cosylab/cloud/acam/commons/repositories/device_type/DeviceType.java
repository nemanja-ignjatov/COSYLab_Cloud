package at.cosylab.cloud.acam.commons.repositories.device_type;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import payloads.acam.deviceTypes.DeviceTypeDTO;
import payloads.acam.deviceTypes.DeviceTypeFunctionality;
import payloads.acam.deviceTypes.DeviceTypeVersionData;
import utils.CloudConstants;

import java.util.List;

public class DeviceType {

    @Id
    private String id;
    @Indexed(unique = true)
    private String typeName;

    private String serviceProvider;
    //Key - Human readable functionality name, Value - Service readable functionality

    private List<DeviceTypeFunctionality> functionalities;

    private DeviceTypeVersionData currentVersion;

    public DeviceType(String typeName, String serviceProvider, List<DeviceTypeFunctionality> functionalities, DeviceTypeVersionData currentVersion) {
        this.typeName = typeName;
        this.serviceProvider = serviceProvider;
        this.functionalities = functionalities;
        if (this.findFunctionalityByServiceName(CloudConstants.FUNCTIONALITY_VIEW_DEVICE) == null) {
            this.functionalities.add(new DeviceTypeFunctionality(
                    CloudConstants.FUNCTIONALITY_VIEW_DEVICE, CloudConstants.FUNCTIONALITY_VIEW_DEVICE));
        }
        this.currentVersion = currentVersion;
    }

    public DeviceType() {
    }

    public DeviceType(DeviceType deviceType) {
        this.id = deviceType.id;
        this.typeName = deviceType.typeName;
        this.serviceProvider = deviceType.serviceProvider;
        this.functionalities = deviceType.functionalities;
        this.currentVersion = deviceType.currentVersion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public List<DeviceTypeFunctionality> getFunctionalities() {
        return functionalities;
    }

    public void setFunctionalities(List<DeviceTypeFunctionality> functionalities) {
        this.functionalities = functionalities;
        if (this.findFunctionalityByServiceName(CloudConstants.FUNCTIONALITY_VIEW_DEVICE) == null) {
            this.functionalities.add(new DeviceTypeFunctionality(
                    CloudConstants.FUNCTIONALITY_VIEW_DEVICE, CloudConstants.FUNCTIONALITY_VIEW_DEVICE));
        }
    }

    public DeviceTypeFunctionality findFunctionalityByServiceName(String serviceName) {
        return this.functionalities.stream().filter(f -> f.getServiceName().equals(serviceName)).findFirst().orElse(null);
    }

    public DeviceTypeVersionData getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(DeviceTypeVersionData currentVersion) {
        this.currentVersion = currentVersion;
    }

    public DeviceTypeDTO toDTO() {
        return new DeviceTypeDTO(this.id, this.typeName, this.serviceProvider, this.functionalities, this.currentVersion);
    }

    public String toString() {
        String str = typeName + " functionality-List:\n";
        for (DeviceTypeFunctionality function : functionalities) {
            str += function + "\n";
        }
        return str;
    }
}
