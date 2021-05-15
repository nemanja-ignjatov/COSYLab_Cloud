package at.cosylab.cloud.acam.services.deviceTypeManagement.payloads;

public class DeleteDeviceTypeRequest {
    private String deviceTypeId;

    public DeleteDeviceTypeRequest() {
    }

    public DeleteDeviceTypeRequest(String deviceTypeId) {

        this.deviceTypeId = deviceTypeId;
    }

    public String getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(String deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    @Override
    public String toString() {
        return "DeleteDeviceTypeRequest{" +
                "deviceTypeId='" + deviceTypeId + '\'' +
                '}';
    }
}
