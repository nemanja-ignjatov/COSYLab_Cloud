package at.cosylab.cloud.tnta.repositories.fogNode;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
@NoArgsConstructor
public class FogNodeEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String identity;
    private String certificate;
    private LocalDateTime certificateRevokedAt;
    @Indexed(unique = true)
    private String aesKeyAlias;
    private String csrToken;
    private String salt;

    private LocalDateTime registeredAt;

    public FogNodeEntity(String identity, String aesKeyAlias, String csrToken, String salt) {
        this.id = id;
        this.identity = identity;
        this.aesKeyAlias = aesKeyAlias;
        this.csrToken = csrToken;
        this.salt = salt;
        this.registeredAt = LocalDateTime.now();
    }
}
