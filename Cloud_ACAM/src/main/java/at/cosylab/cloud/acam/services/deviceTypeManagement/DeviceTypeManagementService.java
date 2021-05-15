package at.cosylab.cloud.acam.services.deviceTypeManagement;

import at.cosylab.cloud.acam.commons.repositories.device_type.DeviceType;
import at.cosylab.cloud.acam.commons.repositories.device_type.DeviceTypeRepository;
import at.cosylab.cloud.acam.commons.repositories.device_type.DeviceTypeVersionRepository;
import at.cosylab.cloud.acam.commons.repositories.device_type.VersionChangeLog;
import at.cosylab.cloud.acam.services.auth.AuthService;
import exceptions.AccountNotFoundException;
import exceptions.DeviceTypeCRUDConflictException;
import exceptions.DeviceTypeNotFoundException;
import exceptions.UnauthorizedAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import payloads.acam.auth.Session;
import payloads.acam.deviceTypes.*;
import utils.CryptoUtilFunctions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceTypeManagementService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceTypeManagementService.class);

    @Autowired
    private DeviceTypeRepository deviceTypeRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private DeviceTypeVersionRepository versionRepo;

    public DeviceType createDeviceType(String tokenString, String name, String serviceProvider,
                                       List<DeviceTypeFunctionality> functionalities, DeviceTypeVersionData versionData) throws DeviceTypeCRUDConflictException, UnauthorizedAccessException, AccountNotFoundException {
        logger.info("[DEVTYPE][CREATEDEVICETYPE] " + name);
        Session session = authService.retrieveSession(tokenString);
        if (deviceTypeRepository.findDeviceTypeByTypeName(name) == null && authService.authorizeDeviceTypeAccess(session)) {
            List<DeviceTypeFunctionality> funcs = functionalities.stream()
                    .map(f -> new DeviceTypeFunctionality(f.getServiceName(), f.getHumanReadableName())).collect(Collectors.toList());
            versionData.setCreatedAt(new Date());
            DeviceType newDeviceType = new DeviceType(name, serviceProvider, funcs, versionData);
            deviceTypeRepository.save(newDeviceType);
            updateDeviceTypeVersion(newDeviceType, null);
            logger.info("[DEVTYPE][CREATEDEVICETYPE] success" + name);
            return newDeviceType;
        } else {
            throw new DeviceTypeCRUDConflictException();
        }
    }

    public List<DeviceTypeDTO> getAllDeviceTypes() {
        logger.info("[DEVTYPE][GETALLDEVICETYPES] ");
        return deviceTypeRepository.findAll().stream().map(dtb -> dtb.toDTO()).collect(Collectors.toList());
    }

    public List<DeviceTypeDTO> getAllDeviceTypesFromServiceProvider(String serviceProvider) {
        logger.info("[DEVTYPE][GETALLDEVICETYPES_SERVICE_PROVIDER] " + serviceProvider);
        return deviceTypeRepository.findDeviceTypeByServiceProvider(serviceProvider).stream().map(dtb -> dtb.toDTO()).collect(Collectors.toList());
    }

    public void removeDeviceType(String tokenString, String devTypeId) throws UnauthorizedAccessException, AccountNotFoundException {
        logger.info("[DEVTYPE][REMOVEDEVICETYPE] " + devTypeId);
        Session session = authService.retrieveSession(tokenString);
        if (authService.authorizeDeviceTypeAccess(session)) {
            DeviceType deviceType = deviceTypeRepository.findDeviceTypeById(devTypeId);
            deviceTypeRepository.deleteById(devTypeId);
            logger.info("[DEVTYPE][REMOVEDEVICETYPE] success" + devTypeId);
        }
    }

    public DeviceType updateDeviceTypeData(String tokenString, String deviceTypeId, String name, String serviceProvider,
                                           List<DeviceTypeFunctionality> functionalities, DeviceTypeVersionData versionData) throws DeviceTypeNotFoundException, DeviceTypeCRUDConflictException, UnauthorizedAccessException, AccountNotFoundException {
        logger.info("[DEVTYPE][UPDATEDEVICETYPEDATA] " + name);
        DeviceType deviceType = deviceTypeRepository.findDeviceTypeById(deviceTypeId);
        DeviceType oldDeviceType = new DeviceType(deviceType);
        String oldName = deviceType.getTypeName();
        Session session = authService.retrieveSession(tokenString);
        if (deviceType != null && authService.authorizeDeviceTypeAccess(session)) {

            if (name != null) {
                //update name
                deviceType.setTypeName(name);
            }

            if (serviceProvider != null) {
                //update name
                deviceType.setServiceProvider(serviceProvider);
            }

            if (functionalities != null) {
                //update functionalities
                deviceType.setFunctionalities(functionalities.stream().map(f -> {
                    if (f.getId() == null) {
                        f.setId(CryptoUtilFunctions.generateUUID());
                    }
                    return f;
                }).collect(Collectors.toList()));
            }

            int currentVersionOrdinal = deviceType.getCurrentVersion().getOrdinal();
            if (versionData != null) {
                versionData.setCreatedAt(new Date());
                deviceType.setCurrentVersion(versionData);
            }
            deviceType.getCurrentVersion().setOrdinal(currentVersionOrdinal + 1);

            deviceTypeRepository.save(deviceType);
            updateDeviceTypeVersion(deviceType, oldDeviceType);
            logger.info("[DEVTYPE][UPDATEDEVICETYPEDATA] success " + name);
            return deviceType;
        } else {
            throw new DeviceTypeNotFoundException();
        }

    }

    public void updateDeviceTypeVersion(DeviceType newDeviceType, DeviceType oldDeviceType) {
        List<DeviceTypeFunctionalityChangeTracker> funcTrackers = new ArrayList<>();

        List<VersionChangeLog> typeVersions = versionRepo.findVersionChangeLogByDeviceTypeId(newDeviceType.getId());
        if (typeVersions == null || typeVersions.size() == 0) {
            funcTrackers = newDeviceType.getFunctionalities().stream().map(
                    f -> new DeviceTypeFunctionalityChangeTracker(f.getId(), f.getServiceName(), f.getHumanReadableName(), DeviceFunctionalityUpdateAction.CREATE)
            ).collect(Collectors.toList());
        } else {

            // find newly added functionalities and track them
            for (DeviceTypeFunctionality func : newDeviceType.getFunctionalities()) {
                DeviceTypeFunctionality functionality = oldDeviceType.getFunctionalities().stream().filter(f -> f.getId().equals(func.getId())).findFirst().orElse(null);
                if (functionality == null) {
                    funcTrackers.add(new DeviceTypeFunctionalityChangeTracker(func.getId(), func.getServiceName(), func.getHumanReadableName(), DeviceFunctionalityUpdateAction.CREATE));
                } else {
                    // if they are already in db -> renamed functionalities, mark their change
                    if (!functionality.getServiceName().equals(func.getServiceName()) || !functionality.getHumanReadableName().equals(func.getHumanReadableName())) {
                        funcTrackers.add(new DeviceTypeFunctionalityChangeTracker(func.getId(), func.getServiceName(), func.getHumanReadableName(), DeviceFunctionalityUpdateAction.CHANGE, functionality.getServiceName()));
                    }
                }
            }
            // find deleted functionalities and stop tracking them
            for (DeviceTypeFunctionality func : oldDeviceType.getFunctionalities()) {
                if (newDeviceType.getFunctionalities().stream().filter(f -> f.getId().equals(func.getId())).findFirst().orElse(null) == null) {
                    funcTrackers.add(new DeviceTypeFunctionalityChangeTracker(func.getId(), func.getServiceName(), func.getHumanReadableName(), DeviceFunctionalityUpdateAction.REMOVE));
                }
            }
        }
        VersionChangeLog vers = new VersionChangeLog(newDeviceType.getId(), newDeviceType.getCurrentVersion().getTitle(),
                newDeviceType.getCurrentVersion().getCreatedAt(), newDeviceType.getCurrentVersion().getDescription(), funcTrackers, newDeviceType.getCurrentVersion().getOrdinal());
        versionRepo.save(vers);

    }

    public ListLatestDeviceTypeResponse listLatestVersionNumbers() {
        return new ListLatestDeviceTypeResponse(deviceTypeRepository.findAll().stream().map(dt -> dt.toDTO()).collect(Collectors.toList()));
    }

    public ListDeviceTypesChangeUpdatesResponse listChangeUpdates(List<DeviceTypeVersionUpdateRequest> requests){
        List<DeviceTypeVersionDTO> retList = new ArrayList<>();

        for(DeviceTypeVersionUpdateRequest item: requests) {
            DeviceType devType = deviceTypeRepository.findDeviceTypeById(item.getDeviceTypeId());
            if(devType != null) {
                List<VersionChangeLogDTO> versionLogs = versionRepo.findVersionChangeLogByDeviceTypeId(item.getDeviceTypeId())
                        .stream().filter(vl -> vl.getOrdinal()>item.getVersionOrdinal())
                        .map(vl -> new VersionChangeLogDTO(vl.getVersionNumber(), vl.getTimestamp(), vl.getChangeLog(), vl.getFunctionalities(), vl.getOrdinal()))
                        .collect(Collectors.toList());

                retList.add(new DeviceTypeVersionDTO(devType.getId(),devType.getTypeName(), devType.getServiceProvider(),versionLogs));
            }
        }
        return new ListDeviceTypesChangeUpdatesResponse(retList);
    }
}
