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

    private String csrToken;

    private LocalDateTime registeredAt;

    public FogNodeEntity(String identity, String aesKeyAlias, String csrToken) {
        this.id = id;
        this.identity = identity;
        this.csrToken = csrToken;
        this.registeredAt = LocalDateTime.now();
    }
}
