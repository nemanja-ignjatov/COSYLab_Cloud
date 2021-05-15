package at.cosylab.cloud.tnta.config.fti;

import at.cosylab.cloud.tnta.config.pki.KeyStoreClient;
import at.cosylab.cloud.tnta.repositories.configurationAttribute.ConfigurationAttribute;
import at.cosylab.cloud.tnta.repositories.configurationAttribute.ConfigurationAttributeRepository;
import identities.CloudServiceType;
import identities.IdentityGenerator;
import lombok.Getter;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import pki.PublicKeyGenerator;
import pki.certificate.CSRHandler;
import pki.certificate.CertificateHelper;
import pki.certificate.CertificateKeyConverter;
import utils.CryptoUtilFunctions;
import utils.ECUtils;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static at.cosylab.cloud.tnta.commons.TNTAGlobals.ID_CONFIG_ATTR;

@Configuration
public class CryptoCredentialsManager {

    private static final Logger logger = LoggerFactory.getLogger(CryptoCredentialsManager.class);

    @Autowired
    private ConfigurationAttributeRepository configurationAttributeRepository;

    @Getter
    @Value("${cosylab.tnta.keystore.filename}")
    private String keystoreFilename;

    @Getter
    @Value("${cosylab.tnta.keystore.password}")
    private String keystorePassword;

    private KeyStoreClient keyStoreClient;

    private Provider bcProvider;

    private PrivateKey myPrivateKey;

    @Getter
    private X509Certificate myCert;

    @PostConstruct
    public void init() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, IOException, UnrecoverableKeyException {

        this.bcProvider = CryptoUtilFunctions.initializeSecurityProvider();
        this.keyStoreClient = new KeyStoreClient(keystoreFilename, keystorePassword);

        this.executeFTIProcedure();

    }

    public X509Certificate signCertificate(PKCS10CertificationRequest csr) throws OperatorCreationException, CertificateException {
        return new CSRHandler(this.myPrivateKey).generateCertificateFromCSR(csr, true, this.myCert);
    }


    private void executeFTIProcedure() {
        boolean certLoaded = false;

        if (!certLoaded) {
            try {

                this.myPrivateKey = keyStoreClient.loadMyPrivateKey();
                this.myCert = keyStoreClient.loadMyCertificate();

                if ((this.myPrivateKey == null) || (this.myCert == null)) {

                    KeyPair keyPair = PublicKeyGenerator.generatePublicKeyPair();

                    String identity = IdentityGenerator.generateCloudServiceIdentifier(CloudServiceType.TNTA);
                    String subjectCN = CryptoUtilFunctions.generateCertCommonName(identity);
                    Certificate tntacert = CertificateHelper.generateSelfSignedCertificate(bcProvider, keyPair, subjectCN);

                    this.myCert = CertificateKeyConverter.convertCertToX509(tntacert);
                    this.myPrivateKey = keyPair.getPrivate();

                    keyStoreClient.storeMyPrivateKeyAndCertificate(keyPair.getPrivate(), this.myCert);
                    logger.info("FTI was successful, keys generated");

                    configurationAttributeRepository.save(new ConfigurationAttribute(ID_CONFIG_ATTR, identity));

                } else {
                    logger.info("FTI was successful, keys already exist");
                }
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String decryptECText(String request) {
        try {
            return ECUtils.decryptText(request,this.myPrivateKey);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException|
                BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }
}
