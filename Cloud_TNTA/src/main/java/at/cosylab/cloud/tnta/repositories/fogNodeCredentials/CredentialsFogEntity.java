package at.cosylab.cloud.tnta.repositories.fogNodeCredentials;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import payloads.tnta.certificate.FogNodeInitialSecretsDTO;
import payloads.tnta.certificate.FogNodeInitialSecretsResponseDTO;

@Data
@NoArgsConstructor
public class CredentialsFogEntity {

    @Id
    private String id;

    private String applicationSecret;
    @Indexed(unique = true)
    private String instanceSecret;
    @Indexed(unique = true)
    private String hash;

    public CredentialsFogEntity(FogNodeInitialSecretsDTO dto){
        this.applicationSecret = dto.getApplicationSecret();
        this.instanceSecret = dto.getInstanceSecret();
        this.hash = DigestUtils.sha256Hex(applicationSecret+instanceSecret);
    }

    public FogNodeInitialSecretsResponseDTO toDTO() {
        return new FogNodeInitialSecretsResponseDTO(this.getId(), this.getApplicationSecret(), this.getInstanceSecret());
    }
}
