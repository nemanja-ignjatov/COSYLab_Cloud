package at.cosylab.cloud.tnta.controllers;

import at.cosylab.cloud.tnta.config.pki.CertificateManager;
import at.cosylab.cloud.tnta.services.ticketsManagement.TicketsValidator;
import exceptions.HTTPExceptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import payloads.tnta.ticket.TicketCreationRequest;
import payloads.tnta.ticket.TicketSignature;

@Controller
@RequestMapping(value = "/tnta/cloud")
public class CloudServicesManagement {

    @Autowired
    private TicketsValidator ticketsValidator;

    @Autowired
    private CertificateManager certificateManager;

    @RequestMapping(method = RequestMethod.POST, value = "/ticket/validate")
    public ResponseEntity<?> validateSignatureInToken(@RequestBody TicketSignature request) {
        try {
            ticketsValidator.validateFogNodeTicket(request);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/ticket/sign")
    public ResponseEntity<?> signRequest(@RequestBody TicketCreationRequest request) {
        try {
            TicketSignature ticket = ticketsValidator.createSignedTicket(request);
            return new ResponseEntity<>(ticket, HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/certificate/get")
    public ResponseEntity<?> getCertificate() {
        try {
            return new ResponseEntity<>(certificateManager.getTNTACertificate(), HttpStatus.OK);
        } catch (Exception e) {
            return HTTPExceptionMapper.convertExceptionToHttpError(e);
        }

    }

}
