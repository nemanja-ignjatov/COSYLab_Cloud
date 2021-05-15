package at.cosylab.cloud.acam.commons.externalClients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import payloads.tnta.ticket.TicketCreationRequest;
import payloads.tnta.ticket.TicketSignature;
import utils.CloudConstants;

import javax.annotation.PostConstruct;
import java.io.Serializable;

@Service
@Slf4j
public class TNTAClient {

    @Autowired
    private LoadBalancerClient loadBalancer;

    @PostConstruct
    public void init() {

    }

    public void validateTicket(TicketSignature ticketSignature) {
        ServiceInstance serviceInstance = loadBalancer.choose(CloudConstants.APP_NAME_TNTA);

        log.debug("Invoking TNTA on URI : {}", serviceInstance.getUri());

        String baseUrl = serviceInstance.getUri().toString();

        baseUrl += CloudConstants.TNTA_ROUTE_VALIDATE_TICKET;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(baseUrl,
                HttpMethod.POST, getHttpEntity(ticketSignature), String.class);

    }

    public TicketSignature createTicket(TicketCreationRequest ticketSignature) {
        ServiceInstance serviceInstance = loadBalancer.choose(CloudConstants.APP_NAME_TNTA);

        log.debug("Invoking TNTA on URI : {}", serviceInstance.getUri());

        String baseUrl = serviceInstance.getUri().toString();

        baseUrl += CloudConstants.TNTA_ROUTE_CREATE_TICKET;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<TicketSignature> response = restTemplate.exchange(baseUrl,
                HttpMethod.POST, getHttpEntity(ticketSignature), TicketSignature.class);

        return response.getBody();

    }

    private static HttpEntity<?> getHttpEntity(Serializable req) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(req, headers);
    }
}
