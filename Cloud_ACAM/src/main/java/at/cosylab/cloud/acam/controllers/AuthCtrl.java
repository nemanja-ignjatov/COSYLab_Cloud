package at.cosylab.cloud.acam.controllers;

import at.cosylab.cloud.acam.services.auth.AuthService;
import at.cosylab.cloud.acam.services.auth.payloads.LoginRequest;
import exceptions.HTTPExceptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import payloads.acam.auth.AccountDataLight;
import payloads.acam.auth.Session;
import utils.CloudConstants;

@Controller
@RequestMapping(value = "/acam/auth")
public class AuthCtrl {

    @Autowired
    private AuthService authService;

    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        try {
            Session sessionData = authService.doLogin(request.getAccountName(), request.getPassword());
            MultiValueMap<String, String> responseHeaders = new HttpHeaders();
            responseHeaders.add(CloudConstants.HEADER_NAME_SET_TOKEN, sessionData.getSessionID());
            return new ResponseEntity<>(new AccountDataLight(sessionData.getAccountName(), sessionData.getAccountRole()), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/logout")
    public ResponseEntity<?> logout(@RequestHeader(CloudConstants.HEADER_NAME_TOKEN) String tokenString) {

        try {
            authService.doLogout(tokenString);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/validate")
    public ResponseEntity<?> validateSession(@RequestHeader(CloudConstants.HEADER_NAME_TOKEN) String tokenString) {
        try {
            Session sessionData = authService.retrieveSession(tokenString);
            return new ResponseEntity<>(new AccountDataLight(sessionData.getAccountName(), sessionData.getAccountRole()), HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }
}
