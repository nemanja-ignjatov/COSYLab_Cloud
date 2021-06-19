package at.cosylab.cloud.tnta.config.pki;

import at.cosylab.cloud.tnta.config.fti.CryptoCredentialsManager;
import exceptions.CSRHandlingException;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pki.certificate.CertificateKeyConverter;
import utils.CryptoUtilFunctions;

import java.io.IOException;
import java.security.cert.CertificateException;

import static utils.CryptoConstants.OID_SHA256_ECDSA;

@Component
@Slf4j
public class CertificateManager {

    @Autowired
    private CryptoCredentialsManager cryptoManager;

    public String handleCSR(String identity, String csrContent) throws CertificateException, OperatorCreationException, IOException, PKCSException, CSRHandlingException {

        PKCS10CertificationRequest csr = CertificateKeyConverter.convertPemToPKCS10CertificationRequest(csrContent);
        verifyCSR(CryptoUtilFunctions.generateCertCommonName(identity), csr);
        return CertificateKeyConverter.convertX509ToPEM(cryptoManager.signCertificate(csr, cryptoManager.getIdentity()));
    }


    public void verifyCSR(String identity, PKCS10CertificationRequest csrObject) throws OperatorCreationException, PKCSException, CSRHandlingException {

        // verify signature algorithm
        if(!csrObject.getSignatureAlgorithm().getAlgorithm().getId().equals(OID_SHA256_ECDSA)) {
            throw new CSRHandlingException("Signature algorithm wrong - " + csrObject.getSignatureAlgorithm().getAlgorithm().getId());
        }

        // verify subject name
        if(!csrObject.getSubject().toString().equals(identity)) {
            throw new CSRHandlingException("Subject name wrong - " + csrObject.getSubject().toString());
        }

        // validate if csr is signed with fn private key
        ContentVerifierProvider prov = new JcaContentVerifierProviderBuilder().build(csrObject.getSubjectPublicKeyInfo());
        if(!csrObject.isSignatureValid(prov)) {
            throw new CSRHandlingException("Signature wrong");
        }


    }

    public String getTNTACertificate() throws IOException {
        return CertificateKeyConverter.convertX509ToPEM(cryptoManager.getMyCert());
    }
}
