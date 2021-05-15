package at.cosylab.cloud.tnta.commons.externalServices;

import exceptions.ServiceNameNotFoundException;
import exceptions.UnauthorizedAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import payloads.acam.auth.AccountDataLight;
import utils.CloudConstants;

import java.io.IOException;


@Component
@Slf4j
public class AuthService {

    @Autowired
    private LoadBalancerClient loadBalancer;

    public AccountDataLight retrieveSession(String tokenString) throws UnauthorizedAccessException, ServiceNameNotFoundException {
        ServiceInstance serviceInstance = loadBalancer.choose(CloudConstants.APP_NAME_ACAM);

        if(serviceInstance == null) {
            throw new ServiceNameNotFoundException();
        }
        String baseUrl = serviceInstance.getUri().toString();

        log.info("Invoking ACAM on URI {}", baseUrl);

        baseUrl += CloudConstants.ACAM_ROUTE_VALIDATE_SESSION;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<AccountDataLight> response = null;

        try {
            return restTemplate.exchange(baseUrl,
                    HttpMethod.GET, getSessionTokenHeaders(tokenString), AccountDataLight.class).getBody();
        } catch (HttpClientErrorException e) {
            throw new UnauthorizedAccessException();
        }
    }

    private static HttpEntity<?> getSessionTokenHeaders(String sessionToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set(CloudConstants.HEADER_NAME_TOKEN, sessionToken);
        return new HttpEntity<>(headers);
    }

}
