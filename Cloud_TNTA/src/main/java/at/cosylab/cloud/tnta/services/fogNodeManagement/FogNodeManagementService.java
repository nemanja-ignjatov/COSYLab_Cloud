package at.cosylab.cloud.tnta.services.fogNodeManagement;

import at.cosylab.cloud.tnta.config.pki.CertificateManager;
import at.cosylab.cloud.tnta.repositories.fogNode.FogNodeEntity;
import at.cosylab.cloud.tnta.repositories.fogNode.FogNodeRepository;
import at.cosylab.cloud.tnta.repositories.fogNodeCredentials.CredentialsFogEntity;
import at.cosylab.cloud.tnta.repositories.fogNodeCredentials.CredentialsFogRepository;
import at.cosylab.cloud.tnta.services.ticketsManagement.TicketsValidator;
import exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import payloads.tnta.certificate.*;
import payloads.tnta.ticket.TicketSignature;
import utils.AESUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FogNodeManagementService {

    @Autowired
    private CredentialsFogRepository credentialsFogRepository;

    @Autowired
    private FogNodeRepository fogRepository;

    @Autowired
    private CertificateManager certificateManager;

    @Autowired
    private TicketsValidator ticketsValidator;

    public List<FogNodeInitialSecretsResponseDTO> registerCredentials(List<FogNodeInitialSecretsDTO> request) {
        List<FogNodeInitialSecretsResponseDTO> retList = new ArrayList<>();
        if (request != null) {
            for (FogNodeInitialSecretsDTO secretsDTO : request) {
                retList.add(credentialsFogRepository.save(new CredentialsFogEntity(secretsDTO)).toDTO());
            }
        }
        return retList;
    }


    public List<FogNodeDTO> getAllFogNodes(String user) {
        log.info("[FOG_NODE][GET_ALL] {} ", user);

        return fogRepository.findAll().stream()
                .map(fn -> new FogNodeDTO(fn.getId(), fn.getIdentity(), fn.getCertificate(), (fn.getCertificateRevokedAt() != null), fn.getRegisteredAt(), fn.getCertificateRevokedAt()))
                .collect(Collectors.toList());
    }

    public RegisterFogNodeResponse registerFogNode(RegisterFogNodeRequest request) throws FogNodeNotFoundException, CryptoHandlingException, KeyStoreHandlingException {

        log.info("[FOG_NODE][REGISTER_FN] {}", request);
        CredentialsFogEntity initialCredentials = credentialsFogRepository.findByHash(request.getHashFogInitialCredentials());
        if (initialCredentials == null) {
            log.error("[FOG_NODE][REGISTER_FN] Initial credentials not found", request.getHashFogNodeSecret());
            throw new FogNodeNotFoundException();
        }

        try {

            String salt = UUID.randomUUID().toString();
            String csrToken = UUID.randomUUID().toString();
            String aesKeyAlias = UUID.randomUUID().toString();
            byte[] iv = salt.getBytes();

            // derive AES key
            SecretKey aesKey = AESUtil.generateAESKey(request.getHashFogNodeSecret(), salt);

            // create CSR token for FN
            String encryptedCSRToken = AESUtil.encryptString(csrToken, aesKey, new IvParameterSpec(iv));

            //Store data to db
            FogNodeEntity fogNodeEntity = new FogNodeEntity(request.getIdentity(), aesKeyAlias, csrToken);
            fogRepository.save(fogNodeEntity);

            // return CSR token
            return new RegisterFogNodeResponse(encryptedCSRToken, salt);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | InvalidAlgorithmParameterException
                | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            log.error("Unable to handle fog node registration ", e);
            throw new CryptoHandlingException(e);
        }
    }

    public FogNodeCSRResponse issueFogNodeCertificate(FogNodeCSR request) throws KeyStoreHandlingException, FogNodeNotFoundException, CSRHandlingException {

        log.info("[FOG_NODE][ISSUE_CERT] {}", request);

        try {
            // validate csr token
            FogNodeEntity fogNodeEntity = fogRepository.findByCsrToken(request.getCsrKey());
            if (fogNodeEntity == null) {
                throw new FogNodeNotFoundException();
            }

            String certificateContent = certificateManager.handleCSR(
                    fogNodeEntity.getIdentity(), request.getCsrContent());
            fogNodeEntity.setCertificate(certificateContent);
            fogRepository.save(fogNodeEntity);
            // verify csr and generate certificate
            return new FogNodeCSRResponse(certificateContent);
        } catch (CertificateException | IOException e) {
            log.error("Unable to issue certificate ", e);
            throw new KeyStoreHandlingException();
        } catch (OperatorCreationException | PKCSException e) {
            log.error("Unable to issue certificate ", e);
            throw new CSRHandlingException(e);
        }
    }

    public RevokeFogNodeCertificate revokeFogNodeCertificate(String user, String fogNodeUuid) {

        log.info("[FOG_NODE][REVOKE_FN_CERT] User-{}, Fog NodeId-{}", user, fogNodeUuid);

        FogNodeEntity fogNodeEntity = fogRepository.findById(fogNodeUuid).orElse(null);

        if (fogNodeEntity != null) {
            fogNodeEntity.setCertificateRevokedAt(LocalDateTime.now());
            fogRepository.save(fogNodeEntity);

            return new RevokeFogNodeCertificate(fogNodeEntity.getId());
        }

        return null;
    }

    public FogNodeDTO getFogNodeInfo(String user, String fogNodeUuid) {

        log.info("[FOG_NODE][GET_NODE_INFO] User-{}, Fog NodeId-{}", user, fogNodeUuid);

        FogNodeEntity fogNodeEntity = fogRepository.findById(fogNodeUuid).orElse(null);
        if (fogNodeEntity != null) {
            return new FogNodeDTO(fogNodeEntity.getId(), fogNodeEntity.getIdentity(),
                    fogNodeEntity.getCertificate(), (fogNodeEntity.getCertificateRevokedAt() != null),
                    fogNodeEntity.getRegisteredAt(), fogNodeEntity.getCertificateRevokedAt());

        }

        return null;
    }


    public List<FogNodeDTO> listRevokedCertificates(ListRevokedCertificatesRequest request, TicketSignature ticket) throws FogNodeNotFoundException, CryptoHandlingException, TicketValidationException {

        log.info("[FOG_NODE][GET_REVOKED] Since: {} ", request.getLastSyncTimestamp());

        ticketsValidator.validateFogNodeTicket(ticket);
        return fogRepository.findAllByCertificateRevokedAtAfter(request.getLastSyncTimestamp()).stream()
                .map(fn -> new FogNodeDTO(fn.getId(), fn.getIdentity(), fn.getCertificate(), (fn.getCertificateRevokedAt() != null), fn.getRegisteredAt(), fn.getCertificateRevokedAt()))
                .collect(Collectors.toList());
    }
}
