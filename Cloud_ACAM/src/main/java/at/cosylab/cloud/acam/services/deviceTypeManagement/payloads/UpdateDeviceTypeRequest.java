package at.cosylab.cloud.acam.services.deviceTypeManagement.payloads;

import payloads.acam.deviceTypes.DeviceTypeFunctionality;
import payloads.acam.deviceTypes.DeviceTypeVersionData;

import java.util.List;

public class UpdateDeviceTypeRequest {
    private String deviceTypeId;
    private String typeName;
    private String serviceProvider;
    private List<DeviceTypeFunctionality> functionalities;
    private DeviceTypeVersionData currentVersion;

    public UpdateDeviceTypeRequest(String deviceTypeId, String typeName, String serviceProvider, List<DeviceTypeFunctionality> functionalities, DeviceTypeVersionData currentVersion) {
        this.deviceTypeId = deviceTypeId;
        this.typeName = typeName;
        this.serviceProvider = serviceProvider;
        this.functionalities = functionalities;
        this.currentVersion = currentVersion;
    }

    public UpdateDeviceTypeRequest() {
    }

    public String getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(String deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
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
    }

    public DeviceTypeVersionData getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(DeviceTypeVersionData currentVersion) {
        this.currentVersion = currentVersion;
    }

    @Override
    public String toString() {
        return "UpdateDeviceTypeRequest{" +
                "deviceTypeId='" + deviceTypeId + '\'' +
                ", typeName='" + typeName + '\'' +
                ", serviceProvider='" + serviceProvider + '\'' +
                ", functionalities=" + functionalities +
                ", currentVersion=" + currentVersion +
                '}';
    }
}

