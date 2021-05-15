package at.cosylab.cloud.acam.services.deviceTypeManagement.payloads;

import payloads.acam.deviceTypes.DeviceTypeDTO;

import java.util.List;

public class AllDeviceTypesResponse {

    private List<DeviceTypeDTO> deviceTypes;

    public AllDeviceTypesResponse() {
    }

    public AllDeviceTypesResponse(List<DeviceTypeDTO> deviceTypes) {
        this.deviceTypes = deviceTypes;
    }

    public List<DeviceTypeDTO> getDeviceTypes() {
        return deviceTypes;
    }

    public void setDeviceTypes(List<DeviceTypeDTO> deviceTypes) {
        this.deviceTypes = deviceTypes;
    }

    @Override
    public String toString() {
        return "AllDeviceTypesResponse{" +
                "deviceTypes=" + deviceTypes +
                '}';
    }
}
