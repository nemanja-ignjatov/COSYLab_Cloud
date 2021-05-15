package at.cosylab.cloud.tnta.controllers;

import at.cosylab.cloud.tnta.commons.externalServices.AuthService;
import at.cosylab.cloud.tnta.config.fti.CryptoCredentialsManager;
import at.cosylab.cloud.tnta.services.fogNodeManagement.FogNodeManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.HTTPExceptionMapper;
import org.bouncycastle.crypto.CryptoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import payloads.acam.auth.AccountDataLight;
import payloads.tnta.certificate.*;
import payloads.tnta.ticket.TicketSignature;
import utils.CloudConstants;
import utils.ECUtils;

import java.util.List;

@Controller
@RequestMapping(value = "/tnta/fog")
public class FogNodeManagementCtrl {

    @Autowired
    private FogNodeManagementService fogNodeManagementService;

    @Autowired
    private AuthService authService;

    @Autowired
    private CryptoCredentialsManager cryptoManager;


    @RequestMapping(method = RequestMethod.POST, value = "/credentials/enter")
    public ResponseEntity<?> registerFogNode(@RequestHeader(CloudConstants.HEADER_NAME_TOKEN) String tokenString,
                                             @RequestBody List<FogNodeInitialSecretsDTO> request) {
        try {
            authService.retrieveSession(tokenString);
            List<FogNodeInitialSecretsResponseDTO> resp = fogNodeManagementService.registerCredentials(request);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/register")
    public ResponseEntity<?> registerFogNode(@RequestBody String requestContent) {
        try {

            String requestText = cryptoManager.decryptECText(requestContent);
            if(requestText == null) {
                throw new CryptoException();
            }
            ObjectMapper objectMapper = new ObjectMapper();
            RegisterFogNodeRequest request = objectMapper.readValue(requestText, RegisterFogNodeRequest.class);
            RegisterFogNodeResponse resp = fogNodeManagementService.registerFogNode(request);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/certificate/issue")
    public ResponseEntity<?> issueFogNodeCertificate(@RequestBody FogNodeCSR request) {
        try {
            FogNodeCSRResponse resp = fogNodeManagementService.issueFogNodeCertificate(request);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/certificate/revoke/{fogNodeUuid}")
    public ResponseEntity<?> revokeCertificate(@RequestHeader(CloudConstants.HEADER_NAME_TOKEN) String tokenString, @PathVariable String fogNodeUuid) {
        try {
            AccountDataLight accountData = authService.retrieveSession(tokenString);
            RevokeFogNodeCertificate resp = fogNodeManagementService.revokeFogNodeCertificate(accountData.getAccountName(), fogNodeUuid);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = CloudConstants.REVOCATION_LIST_URI)
    public ResponseEntity<?> listRevokedCertificates(@RequestHeader(CloudConstants.HEADER_TICKET_TOKEN) String ticketToken,
                                                     @RequestHeader(CloudConstants.HEADER_TICKET_ISSUER) String ticketIssuer,
                                                     @PathVariable ListRevokedCertificatesRequest request) {
        try {
            ObjectMapper objMapper = new ObjectMapper();
            TicketSignature ticketSignature = new TicketSignature(ticketToken, objMapper.writeValueAsString(request), ticketIssuer, CloudConstants.REVOCATION_LIST_URI );
            List<FogNodeDTO> resp = fogNodeManagementService.listRevokedCertificates(request, ticketSignature);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public ResponseEntity<?> getAllFogNodes(@RequestHeader(CloudConstants.HEADER_NAME_TOKEN) String tokenString) {
        try {
            authService.retrieveSession(tokenString);
            return new ResponseEntity<List<FogNodeDTO>>(fogNodeManagementService.getAllFogNodes(tokenString), HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get/{fogNodeUuid}")
    public ResponseEntity<?> getFogNodeInfo(@RequestHeader(CloudConstants.HEADER_NAME_TOKEN) String tokenString, @PathVariable String fogNodeUuid) {
        try {
            AccountDataLight accountDataLight = authService.retrieveSession(tokenString);
            return new ResponseEntity<FogNodeDTO>(fogNodeManagementService.getFogNodeInfo(accountDataLight.getAccountName(), fogNodeUuid), HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }

}
