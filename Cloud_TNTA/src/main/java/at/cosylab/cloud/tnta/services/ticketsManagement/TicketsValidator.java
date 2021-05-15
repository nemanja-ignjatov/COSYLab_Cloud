package at.cosylab.cloud.tnta.services.ticketsManagement;

import at.cosylab.cloud.tnta.commons.TNTAGlobals;
import at.cosylab.cloud.tnta.config.fti.CryptoCredentialsManager;
import at.cosylab.cloud.tnta.config.pki.KeyStoreClient;
import at.cosylab.cloud.tnta.repositories.configurationAttribute.ConfigurationAttribute;
import at.cosylab.cloud.tnta.repositories.configurationAttribute.ConfigurationAttributeRepository;
import at.cosylab.cloud.tnta.repositories.fogNode.FogNodeEntity;
import at.cosylab.cloud.tnta.repositories.fogNode.FogNodeRepository;
import exceptions.CryptoHandlingException;
import exceptions.FogNodeNotFoundException;
import exceptions.TicketValidationException;
import jws.JWSTokenHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import payloads.tnta.ticket.TicketCreationRequest;
import payloads.tnta.ticket.TicketSignature;
import payloads.tnta.ticket.TicketType;
import pki.certificate.CertificateKeyConverter;
import utils.CloudConstants;
import utils.CryptoConstants;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class TicketsValidator {

    @Autowired
    private FogNodeRepository fogNodeRepository;

    @Autowired
    private CryptoCredentialsManager cryptoManager;

    @Autowired
    private ConfigurationAttributeRepository configurationAttributeRepository;

    public TicketSignature createSignedTicket(TicketCreationRequest request) throws CryptoHandlingException {

        try {
            String messageHash = DigestUtils.sha256Hex(request.getMessageJson());

            KeyStoreClient keyStoreClient = new KeyStoreClient(cryptoManager.getKeystoreFilename(), cryptoManager.getKeystorePassword());

            ConfigurationAttribute attr = configurationAttributeRepository.findByName(TNTAGlobals.ID_CONFIG_ATTR);

            JWSTokenHandler jwsHandler = new JWSTokenHandler(keyStoreClient.loadMyCertificate().getPublicKey(),
                    keyStoreClient.loadMyPrivateKey(), attr.getValue(), request.getSubjectId());

            Map<String, String> claims = new HashMap<>();
            claims.put(CloudConstants.TICKET_FIELD_TYPE, request.getTicketType().toString());
            claims.put(CloudConstants.TICKET_FIELD_FUNCTIONALITY, request.getFunctionality());
            claims.put(CloudConstants.TICKET_FIELD_MESSAGE_HASH, messageHash);

            return new TicketSignature(jwsHandler.generateJWSToken(claims), request.getMessageJson(), attr.getValue(), request.getFunctionality());

        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | NoSuchProviderException
                | UnrecoverableKeyException | IOException e) {
            throw new CryptoHandlingException(e);
        }
    }

    public void validateFogNodeTicket(TicketSignature request) throws FogNodeNotFoundException, TicketValidationException, CryptoHandlingException {

        try {

            FogNodeEntity fogNodeEntity = fogNodeRepository.findByIdentity(request.getIssuerId());
            if (fogNodeEntity == null) {
                throw new FogNodeNotFoundException();
            }

            validateFogNodeCertificate(fogNodeEntity);

            ConfigurationAttribute configAttr = configurationAttributeRepository.findByName(TNTAGlobals.ID_CONFIG_ATTR);

            KeyStoreClient keyStoreClient = new KeyStoreClient(cryptoManager.getKeystoreFilename(), cryptoManager.getKeystorePassword());

            // validate JWS signature
            // extract data from JWS
            JWSTokenHandler jwsHandler = new JWSTokenHandler(
                    CertificateKeyConverter.convertPEMToX509(fogNodeEntity.getCertificate()).getPublicKey(),
                    keyStoreClient.loadMyPrivateKey(), fogNodeEntity.getIdentity(), configAttr.getValue());

            // Check claims
            Map<String, Object> claims = jwsHandler.getClaimsFromJWS(request.getJwsEncoded());

            // check issueId
            if (!claims.get(CryptoConstants.ISSUER_JWS_NAME).equals(request.getIssuerId())) {
                throw new TicketValidationException();
            }
            // check expected ticketType
            if (!claims.get(CloudConstants.TICKET_FIELD_TYPE).equals(TicketType.REQUEST.toString())) {
                throw new TicketValidationException();
            }
            // check expected functionality
            if (!claims.get(CloudConstants.TICKET_FIELD_FUNCTIONALITY).equals(request.getFunctionality())) {
                throw new TicketValidationException();
            }

            // check message hash
            if (!claims.get(CloudConstants.TICKET_FIELD_MESSAGE_HASH).equals(DigestUtils.sha256Hex(request.getMessageJson()))) {
                throw new TicketValidationException();
            }

        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | NoSuchProviderException | IOException
                | UnrecoverableKeyException e) {
            throw new CryptoHandlingException(e);
        }

    }

    private void validateFogNodeCertificate(FogNodeEntity fogNodeEntity) throws IOException, CertificateException {
        if (fogNodeEntity.getCertificateRevokedAt() != null) {
                throw new CertificateException();
        }

        CertificateKeyConverter.convertPEMToX509(fogNodeEntity.getCertificate()).checkValidity();

    }
}
