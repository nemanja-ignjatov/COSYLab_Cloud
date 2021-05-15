package at.cosylab.cloud.acam.services.deviceTypeManagement.payloads;

import payloads.acam.deviceTypes.DeviceTypeFunctionality;
import payloads.acam.deviceTypes.DeviceTypeVersionData;

import java.util.List;

public class CreateDeviceTypeRequest {
    private String deviceTypeName;
    private String serviceProvider;
    private List<DeviceTypeFunctionality> functionalities;
    private DeviceTypeVersionData currentVersion;

    public CreateDeviceTypeRequest(String deviceTypeName, String serviceProvider, List<DeviceTypeFunctionality> functionalities, DeviceTypeVersionData currentVersion) {
        this.deviceTypeName = deviceTypeName;
        this.serviceProvider = serviceProvider;
        this.functionalities = functionalities;
        this.currentVersion = currentVersion;
    }

    public CreateDeviceTypeRequest() {
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
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
}
