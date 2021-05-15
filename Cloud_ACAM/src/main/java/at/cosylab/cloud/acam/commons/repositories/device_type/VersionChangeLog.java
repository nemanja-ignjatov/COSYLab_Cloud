package at.cosylab.cloud.acam.commons.repositories.device_type;

import org.springframework.data.annotation.Id;
import payloads.acam.deviceTypes.DeviceTypeFunctionalityChangeTracker;

import java.util.Date;
import java.util.List;

public class VersionChangeLog {

    @Id
    private String id;

    private String deviceTypeId;

    private String versionNumber;

    private Date timestamp;

    private String changeLog;

    private List<DeviceTypeFunctionalityChangeTracker> functionalities;

    private int ordinal;

    public VersionChangeLog() {
    }

    public VersionChangeLog(String deviceTypeId, String versionNumber, Date timestamp, String changeLog, List<DeviceTypeFunctionalityChangeTracker> functionalities, int ordinal) {
        this.deviceTypeId = deviceTypeId;
        this.versionNumber = versionNumber;
        this.timestamp = timestamp;
        this.changeLog = changeLog;
        this.functionalities = functionalities;
        this.ordinal = ordinal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(String deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }

    public List<DeviceTypeFunctionalityChangeTracker> getFunctionalities() {
        return functionalities;
    }

    public void setFunctionalities(List<DeviceTypeFunctionalityChangeTracker> functionalities) {
        this.functionalities = functionalities;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    @Override
    public String toString() {
        return "VersionChangeLog{" +
                "id='" + id + '\'' +
                ", deviceTypeId='" + deviceTypeId + '\'' +
                ", versionNumber='" + versionNumber + '\'' +
                ", timestamp=" + timestamp +
                ", changeLog='" + changeLog + '\'' +
                ", functionalities=" + functionalities +
                ", ordinal=" + ordinal +
                '}';
    }
}
