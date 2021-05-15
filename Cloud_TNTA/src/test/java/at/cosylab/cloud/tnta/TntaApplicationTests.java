package at.cosylab.cloud.tnta;

import at.cosylab.cloud.tnta.commons.TNTAGlobals;
import at.cosylab.cloud.tnta.repositories.configurationAttribute.ConfigurationAttributeRepository;
import at.cosylab.cloud.tnta.repositories.fogNode.FogNodeEntity;
import at.cosylab.cloud.tnta.repositories.fogNode.FogNodeRepository;
import at.cosylab.cloud.tnta.repositories.fogNodeCredentials.CredentialsFogRepository;
import at.cosylab.cloud.tnta.services.fogNodeManagement.FogNodeManagementService;
import at.cosylab.cloud.tnta.services.ticketsManagement.TicketsValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.*;
import identities.FogServiceType;
import identities.IdentityGenerator;
import jws.JWSTokenHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import payloads.tnta.certificate.*;
import payloads.tnta.ticket.TicketCreationRequest;
import payloads.tnta.ticket.TicketSignature;
import payloads.tnta.ticket.TicketType;
import pki.PublicKeyGenerator;
import pki.certificate.CSRHandler;
import pki.certificate.CertificateKeyConverter;
import utils.AESUtil;
import utils.CloudConstants;
import utils.CryptoUtilFunctions;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class TntaApplicationTests {

    private final String defaultUser = "user";
    private final String applicationSecret = "as";
    private final String instanceSecret = "is";
    private final String userSecret = "us";

    private final String applicationSecretSecond = "as2";
    private final String instanceSecretSecond = "is2";
    @Autowired
    private FogNodeManagementService fogService;

    @Autowired
    private FogNodeRepository fogRepository;

    @Autowired
    private CredentialsFogRepository credentialsRepository;

    @Autowired
    private TicketsValidator ticketsValidator;

    @Autowired
    private ConfigurationAttributeRepository configurationAttributeRepository;

    @BeforeEach
    void initTest() {
        fogRepository.deleteAll();
        credentialsRepository.deleteAll();

        List<FogNodeInitialSecretsDTO> credentialsList = List.of(
                new FogNodeInitialSecretsDTO(applicationSecret, instanceSecret),
                new FogNodeInitialSecretsDTO(applicationSecretSecond, instanceSecretSecond)
        );
        fogService.registerCredentials(credentialsList);

    }

    @AfterEach
    void cleanTest() {
        fogRepository.deleteAll();
        credentialsRepository.deleteAll();
    }


    @Test
    void fogTestScenario() throws FogNodeNotFoundException, CryptoHandlingException, KeyStoreHandlingException, CSRHandlingException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, OperatorCreationException, IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {

        KeyPair keyPair = PublicKeyGenerator.generatePublicKeyPair();
        String ftaId = CryptoUtilFunctions.generateUUID();
        String identity = IdentityGenerator.generateFogServiceIdentifier(ftaId, FogServiceType.FTA);


        String hashFogSecret = DigestUtils.sha256Hex(applicationSecret + instanceSecret + userSecret);
        String hashFogCredentials = DigestUtils.sha256Hex(applicationSecret + instanceSecret);

        RegisterFogNodeRequest registerRequest = new RegisterFogNodeRequest(hashFogCredentials, hashFogSecret, identity);
        RegisterFogNodeResponse registerResponse = fogService.registerFogNode(registerRequest);

        IvParameterSpec ivspec = new IvParameterSpec(registerResponse.getSalt().getBytes(StandardCharsets.UTF_8));

        SecretKey secretKey = AESUtil.generateAESKey(hashFogSecret, registerResponse.getSalt());

        PKCS10CertificationRequest request = CSRHandler.generateCSR(keyPair.getPrivate(), keyPair.getPublic(), CryptoUtilFunctions.generateCertCommonName(identity));

        String csrContent = CertificateKeyConverter.convertPKCS10CertificationRequestToPEM(request);
        String csrKey = AESUtil.decryptString(registerResponse.getCsrKey(), secretKey, ivspec);

        FogNodeCSR csr = new FogNodeCSR(csrContent, csrKey);

        FogNodeCSRResponse csrResponse = fogService.issueFogNodeCertificate(csr);

        FogNodeEntity fogNodeEntity = fogRepository.findByCsrToken(csrKey);

        Assertions.assertEquals(fogNodeEntity.getCertificate(), csrResponse.getCertificateContent());
    }

    @Test
    void testRevocationTickerValid() throws KeyStoreHandlingException, CSRHandlingException, CryptoHandlingException, InvalidKeySpecException, FogNodeNotFoundException, OperatorCreationException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchPaddingException, IOException, NoSuchProviderException, IllegalBlockSizeException, InterruptedException, TicketValidationException {

        FogNodeTestCredentials fnData = registerFogNode(applicationSecret, instanceSecret, userSecret);
        FogNodeTestCredentials fnData2 = registerFogNode(applicationSecretSecond, instanceSecretSecond, userSecret);

        ObjectMapper objectMapper = new ObjectMapper();
        LocalDateTime now = LocalDateTime.now();

        // get revocation list
        ListRevokedCertificatesRequest request = new ListRevokedCertificatesRequest(now);

        String messageJson = objectMapper.writeValueAsString(request);

        JWSTokenHandler jwsHandler = new JWSTokenHandler(fnData.getKeypair().getPublic(), fnData.getKeypair().getPrivate(), fnData.getIdentity(),
                configurationAttributeRepository.findByName(TNTAGlobals.ID_CONFIG_ATTR).getValue());

        Map<String, String> claims = new HashMap<>();
        claims.put(CloudConstants.TICKET_FIELD_TYPE, TicketType.REQUEST.toString());
        claims.put(CloudConstants.TICKET_FIELD_FUNCTIONALITY, CloudConstants.REVOCATION_LIST_URI);
        claims.put(CloudConstants.TICKET_FIELD_MESSAGE_HASH, DigestUtils.sha256Hex(messageJson));

        TicketSignature ticketSignature = new TicketSignature(jwsHandler.generateJWSToken(claims), messageJson, fnData.getIdentity(), CloudConstants.REVOCATION_LIST_URI);

        List<FogNodeDTO> crl = fogService.listRevokedCertificates(request,ticketSignature);
        Assertions.assertEquals(crl.size(),0);
        // revoke cert
        fogService.revokeFogNodeCertificate(defaultUser, fnData2.getId());
        // get revocation list -> should fail since cert is invalid
        // Make sure that revocation occurs after last sync
        Thread.sleep(100);


        crl = fogService.listRevokedCertificates(new ListRevokedCertificatesRequest(now), ticketSignature);

        Assertions.assertEquals(crl.size(),1);
        Assertions.assertEquals(crl.get(0).getIdentity(),fnData2.getIdentity());
    }

    @Test
    void testRevocationTicketFailure() throws KeyStoreHandlingException, CSRHandlingException, CryptoHandlingException, InvalidKeySpecException, FogNodeNotFoundException, OperatorCreationException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchPaddingException, IOException, NoSuchProviderException, IllegalBlockSizeException, InterruptedException, TicketValidationException {

        // register 1 FN
        FogNodeTestCredentials fnData = registerFogNode(applicationSecret,instanceSecret,userSecret);

        ObjectMapper objectMapper = new ObjectMapper();
        LocalDateTime now = LocalDateTime.now();

        // get revocation list
        ListRevokedCertificatesRequest request = new ListRevokedCertificatesRequest(now);

        String messageJson = objectMapper.writeValueAsString(request);

        JWSTokenHandler jwsHandler = new JWSTokenHandler(fnData.getKeypair().getPublic(), fnData.getKeypair().getPrivate(), fnData.getIdentity(),
                configurationAttributeRepository.findByName(TNTAGlobals.ID_CONFIG_ATTR).getValue());

        Map<String, String> claims = new HashMap<>();
        claims.put(CloudConstants.TICKET_FIELD_TYPE, TicketType.REQUEST.toString());
        claims.put(CloudConstants.TICKET_FIELD_FUNCTIONALITY, CloudConstants.REVOCATION_LIST_URI);
        claims.put(CloudConstants.TICKET_FIELD_MESSAGE_HASH, DigestUtils.sha256Hex(messageJson));

        TicketSignature ticketSignature = new TicketSignature(jwsHandler.generateJWSToken(claims), messageJson, fnData.getIdentity(), CloudConstants.REVOCATION_LIST_URI);

        List<FogNodeDTO> crl = fogService.listRevokedCertificates(request,ticketSignature);

        // revoke cert
        fogService.revokeFogNodeCertificate(defaultUser, fnData.getId());
        // get revocation list -> should fail since cert is invalid
        // Make sure that revocation occurs after last sync
        Thread.sleep(100);

        Assertions.assertThrows(CryptoHandlingException.class, () ->
        {
            fogService.listRevokedCertificates(new ListRevokedCertificatesRequest(now), ticketSignature);
        });
    }

    @Test
    void testGenerateTicket() throws IOException, CryptoHandlingException, FogNodeNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, CSRHandlingException, InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, OperatorCreationException, NoSuchProviderException, KeyStoreHandlingException, TicketValidationException {

        FogNodeTestCredentials fnData = registerFogNode(applicationSecret,instanceSecret,userSecret);
        FogNodeEntity fnEntity = fogRepository.findById(fnData.getId()).orElse(null);

        ObjectMapper objectMapper = new ObjectMapper();

        String messageJson = objectMapper.writeValueAsString(new FogNodeCSR("csrContent", "csrKey"));

        ticketsValidator.createSignedTicket(
                new TicketCreationRequest(messageJson, "testFunctionality", TicketType.RESPONSE, fnEntity.getIdentity()));

    }

    @Test
    void testValidateTicket() throws FogNodeNotFoundException, CryptoHandlingException, TicketValidationException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, KeyStoreHandlingException, InvalidKeySpecException, IOException, OperatorCreationException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, CSRHandlingException {

        FogNodeTestCredentials fnData = registerFogNode(applicationSecret,instanceSecret,userSecret);

        ObjectMapper objectMapper = new ObjectMapper();
        String messageJson = objectMapper.writeValueAsString(new FogNodeCSR("csrContent", "csrKey"));

        JWSTokenHandler jwsHandler = new JWSTokenHandler(fnData.getKeypair().getPublic(), fnData.getKeypair().getPrivate(), fnData.getIdentity(),
                configurationAttributeRepository.findByName(TNTAGlobals.ID_CONFIG_ATTR).getValue());

        Map<String, String> claims = new HashMap<>();
        claims.put(CloudConstants.TICKET_FIELD_TYPE, TicketType.REQUEST.toString());
        claims.put(CloudConstants.TICKET_FIELD_FUNCTIONALITY, "csr");
        claims.put(CloudConstants.TICKET_FIELD_MESSAGE_HASH, DigestUtils.sha256Hex(messageJson));

        TicketSignature ticketSignature = new TicketSignature(jwsHandler.generateJWSToken(claims), messageJson, fnData.getIdentity(), "csr");

        ticketsValidator.validateFogNodeTicket(ticketSignature);
    }

    private FogNodeTestCredentials registerFogNode(String as, String is, String us) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, FogNodeNotFoundException, CryptoHandlingException, KeyStoreHandlingException, InvalidKeySpecException, OperatorCreationException, IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, CSRHandlingException {

        KeyPair keyPair = PublicKeyGenerator.generatePublicKeyPair();
        String ftaId = CryptoUtilFunctions.generateUUID();

        String identity = IdentityGenerator.generateFogServiceIdentifier(ftaId, FogServiceType.FTA);

        String hashFogSecret = DigestUtils.sha256Hex(as + is + us);
        String hashFogCredentials = DigestUtils.sha256Hex(as + is);

        RegisterFogNodeRequest registerRequest = new RegisterFogNodeRequest(hashFogCredentials, hashFogSecret, identity);
        RegisterFogNodeResponse registerResponse = fogService.registerFogNode(registerRequest);

        IvParameterSpec ivspec = new IvParameterSpec(registerResponse.getSalt().getBytes(StandardCharsets.UTF_8));

        SecretKey secretKey = AESUtil.generateAESKey(hashFogSecret, registerResponse.getSalt());

        PKCS10CertificationRequest request = CSRHandler.generateCSR(keyPair.getPrivate(), keyPair.getPublic(), CryptoUtilFunctions.generateCertCommonName(identity));

        String csrContent = CertificateKeyConverter.convertPKCS10CertificationRequestToPEM(request);
        String csrKey = AESUtil.decryptString(registerResponse.getCsrKey(), secretKey, ivspec);

        FogNodeCSR csr = new FogNodeCSR(csrContent, csrKey);

        fogService.issueFogNodeCertificate(csr);

        FogNodeEntity fogNodeEntity = fogRepository.findByCsrToken(csrKey);

        return new FogNodeTestCredentials(fogNodeEntity.getId(), identity, keyPair);

    }

    @Data
    @AllArgsConstructor
    private class FogNodeTestCredentials {
        private String id;
        private String identity;
        private KeyPair keypair;
    }

}
