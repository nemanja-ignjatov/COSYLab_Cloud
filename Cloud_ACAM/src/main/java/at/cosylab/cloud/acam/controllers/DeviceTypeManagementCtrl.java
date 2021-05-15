package at.cosylab.cloud.acam.controllers;

import at.cosylab.cloud.acam.commons.externalClients.TNTAClient;
import at.cosylab.cloud.acam.commons.repositories.device_type.DeviceType;
import at.cosylab.cloud.acam.services.deviceTypeManagement.DeviceTypeManagementService;
import at.cosylab.cloud.acam.services.deviceTypeManagement.payloads.AllDeviceTypesResponse;
import at.cosylab.cloud.acam.services.deviceTypeManagement.payloads.CreateDeviceTypeRequest;
import at.cosylab.cloud.acam.services.deviceTypeManagement.payloads.UpdateDeviceTypeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.HTTPExceptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import payloads.acam.deviceTypes.ListDeviceTypesChangeUpdatesRequest;
import payloads.acam.deviceTypes.ListDeviceTypesChangeUpdatesResponse;
import payloads.acam.deviceTypes.ListLatestDeviceTypeResponse;
import payloads.tnta.ticket.TicketCreationRequest;
import payloads.tnta.ticket.TicketSignature;
import payloads.tnta.ticket.TicketType;
import utils.CloudConstants;

@Controller
@RequestMapping(value = "/acam/devicetype")
public class DeviceTypeManagementCtrl {

    @Autowired
    private DeviceTypeManagementService devTypeService;

    @Autowired
    private TNTAClient tntaClient;

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public ResponseEntity<?> createDeviceType(@RequestHeader(CloudConstants.HEADER_NAME_TOKEN) String tokenString, @RequestBody CreateDeviceTypeRequest request) {
        try {
            DeviceType devType = devTypeService.createDeviceType(tokenString, request.getDeviceTypeName(),
                    request.getServiceProvider(), request.getFunctionalities(), request.getCurrentVersion());
            return new ResponseEntity<DeviceType>(devType, HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public ResponseEntity<?> getAllDeviceTypes() {
        try {
            return new ResponseEntity<AllDeviceTypesResponse>(new AllDeviceTypesResponse(devTypeService.getAllDeviceTypes()), HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/listServiceProvider/{serviceProvider}")
    public ResponseEntity<?> getAllDeviceTypesFromServiceProvider(@PathVariable(value = "serviceProvider") String serviceProvider) {
        try {
            return new ResponseEntity<>(new AllDeviceTypesResponse(devTypeService.getAllDeviceTypesFromServiceProvider(serviceProvider)), HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete/{devTypeId}")
    public ResponseEntity<?> deleteDeviceType(@RequestHeader(CloudConstants.HEADER_NAME_TOKEN) String tokenString, @PathVariable("devTypeId") String devTypeId) {
        try {
            devTypeService.removeDeviceType(tokenString, devTypeId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    public ResponseEntity<?> updateDeviceType(@RequestHeader(CloudConstants.HEADER_NAME_TOKEN) String tokenString, @RequestBody UpdateDeviceTypeRequest request) {
        try {
            return new ResponseEntity<>(devTypeService.updateDeviceTypeData(tokenString, request.getDeviceTypeId(),
                    request.getTypeName(), request.getServiceProvider(), request.getFunctionalities(), request.getCurrentVersion()), HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/listLatestDeviceTypeVersions")
    public ResponseEntity<?> listLatestDeviceTypeVersions(@RequestHeader(CloudConstants.HEADER_TICKET_TOKEN) String ticketToken,
                                                          @RequestHeader(CloudConstants.HEADER_TICKET_ISSUER) String ticketIssuer,
                                                          @RequestHeader(CloudConstants.HEADER_REQUEST_SENDER) String requestSubject) {
        try {

            ObjectMapper objMapper = new ObjectMapper();
            // validate received ticket
            TicketSignature ticketSignature = new TicketSignature(ticketToken, "", ticketIssuer, CloudConstants.ACAM_GET_DEVICE_TYPES_VERSION);
            tntaClient.validateTicket(ticketSignature);

            // get request
            ListLatestDeviceTypeResponse response = devTypeService.listLatestVersionNumbers();

            // create response ticket and set it as headers
            TicketCreationRequest ticketRequest = new TicketCreationRequest(objMapper.writeValueAsString(response), CloudConstants.ACAM_GET_DEVICE_TYPES_VERSION,
                    TicketType.RESPONSE, requestSubject);

            TicketSignature responseTicket = tntaClient.createTicket(ticketRequest);
            MultiValueMap<String, String> responseHeaders = new HttpHeaders();
            responseHeaders.add(CloudConstants.HEADER_TICKET_TOKEN, responseTicket.getJwsEncoded());
            responseHeaders.add(CloudConstants.HEADER_TICKET_ISSUER, responseTicket.getIssuerId());
            return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/listChangeUpdates")
    public ResponseEntity<?> listChangeUpdates(@RequestHeader(CloudConstants.HEADER_TICKET_TOKEN) String ticketToken,
                                               @RequestHeader(CloudConstants.HEADER_TICKET_ISSUER) String ticketIssuer,
                                               @RequestHeader(CloudConstants.HEADER_REQUEST_SENDER) String requestSubject,
                                               @RequestBody ListDeviceTypesChangeUpdatesRequest request) {

        try {
            ObjectMapper objMapper = new ObjectMapper();
            TicketSignature ticketSignature = new TicketSignature(ticketToken, objMapper.writeValueAsString(request), ticketIssuer, CloudConstants.ACAM_LIST_DEVICE_CHANGES);
            tntaClient.validateTicket(ticketSignature);

            ListDeviceTypesChangeUpdatesResponse response = devTypeService.listChangeUpdates(request.getRequestItems());

            // create response ticket and set it as headers
            TicketCreationRequest ticketRequest = new TicketCreationRequest(objMapper.writeValueAsString(response), CloudConstants.ACAM_LIST_DEVICE_CHANGES,
                    TicketType.RESPONSE, requestSubject);

            TicketSignature responseTicket = tntaClient.createTicket(ticketRequest);
            MultiValueMap<String, String> responseHeaders = new HttpHeaders();
            responseHeaders.add(CloudConstants.HEADER_TICKET_TOKEN, responseTicket.getJwsEncoded());
            responseHeaders.add(CloudConstants.HEADER_TICKET_ISSUER, responseTicket.getIssuerId());
            return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);

        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }
}
